package ai.ecma.auth.service;

import ai.ecma.auth.common.MessageService;
import ai.ecma.auth.exception.RestException;
import ai.ecma.auth.payload.*;
import ai.ecma.auth.security.JwtProvider;
import ai.ecma.lib.entity.District;
import ai.ecma.lib.entity.Role;
import ai.ecma.lib.entity.User;
import ai.ecma.lib.entity.VerificationCode;
import ai.ecma.lib.enums.RoleTypeEnum;
import ai.ecma.lib.payload.ApiResult;
import ai.ecma.lib.repository.DistrictRepository;
import ai.ecma.lib.repository.RoleRepository;
import ai.ecma.lib.repository.UserRepository;
import ai.ecma.lib.repository.VerificationCodeRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * This class not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 19.01.2022
 */
@Service
//@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Value("${verification-code.expire-time}")
    private Long verificationExpireTime;

    @Value("${verification-code.limit}")
    private Integer limit;

    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final RoleRepository roleRepository;
    private final SmsService smsService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final DistrictRepository districtRepository;

    public AuthServiceImpl(UserRepository userRepository, VerificationCodeRepository verificationCodeRepository, RoleRepository roleRepository, SmsService smsService, @Lazy AuthenticationManager authenticationManager, @Lazy JwtProvider jwtProvider, DistrictRepository districtRepository) {
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.roleRepository = roleRepository;
        this.smsService = smsService;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.districtRepository = districtRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username).orElseThrow(RestException::forbidden);
        return new UserPrincipal(user);
    }

    @Override
    public ApiResult<?> checkPhone(CheckPhoneDto dto) throws RestException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - verificationExpireTime);
        List<VerificationCode> verificationCodes = verificationCodeRepository.findAllByCreatedAtAfterOrderByCreatedAt(timestamp);

        if (verificationCodes.size() >= limit) {
            throw RestException.restThrow(MessageService.getMessage("MESSAGE_NOT_ENOUGH_LIMIT"), HttpStatus.BAD_REQUEST);
        }

        if (!verificationCodes.isEmpty()) {
            VerificationCode lastVerificationCode = verificationCodes.get(0);
            if (!lastVerificationCode.isUsed() && lastVerificationCode.getExpireTime().after(new Timestamp(System.currentTimeMillis()))) {
                throw RestException.restThrow(MessageService.getMessage("LAST_VERIFICATION_CODE_NOT_EXPIRED"), HttpStatus.BAD_REQUEST);
            }
        }

        String verificationCode = generateCode();
        smsService.sendMessage(dto.getPhoneNumber(), verificationCode);

        verificationCodeRepository.save(new VerificationCode(dto.getPhoneNumber(), verificationCode));
        return ApiResult.successResponse(MessageService.getMessage("SMS_SENT"));
    }

    @Override
    public ApiResult<?> verify(PhoneVerifyDto dto) {
        VerificationCode verificationCode = verificationCodeRepository.checkVerificationCode(dto.getPhoneNumber(), dto.getVerificationCode(), new Timestamp(System.currentTimeMillis()))
                .orElseThrow(() -> RestException.restThrow(MessageService.getMessage("VERIFICATION_CODE_NOT_AVAILABLE"), HttpStatus.BAD_REQUEST));

        Optional<User> optionalUser = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        if (optionalUser.isPresent()) {
            String accessToken = jwtProvider.generateToken(dto.getPhoneNumber(), true);
            String refreshToken = jwtProvider.generateToken(dto.getPhoneNumber(), false);
            verificationCode.setUsed(true);
            verificationCodeRepository.save(verificationCode);
            return ApiResult.successResponse(new CheckUserRegisterDto(accessToken, refreshToken));
        } else {

            return ApiResult.successResponse(new CheckUserRegisterDto());
        }
    }

    @Override
    public ApiResult<?> signUp(RegisterDto dto) {
        VerificationCode verificationCode = verificationCodeRepository.checkVerificationCode(dto.getPhoneNumber(), dto.getVerificationCode(), new Timestamp(System.currentTimeMillis()))
                .orElseThrow(() -> RestException.restThrow(MessageService.getMessage("VERIFICATION_CODE_NOT_AVAILABLE"), HttpStatus.BAD_REQUEST));

        Role roleUser = roleRepository.findByRoleType(RoleTypeEnum.USER)
                .orElseThrow(() -> RestException.restThrow(MessageService.getMessage("ROLE_NOT_FOUND"), HttpStatus.NOT_FOUND));

        District district = districtRepository.findById(dto.getDistrictId()).orElseThrow(() -> RestException.notFound("DISTRICT"));
        User user = new User(dto.getPhoneNumber(), dto.getFirstName(), dto.getLastName(), district, dto.getLanguage(), roleUser);
        userRepository.save(user);

        verificationCode.setUsed(true);
        verificationCodeRepository.save(verificationCode);

        String accessToken = jwtProvider.generateToken(dto.getPhoneNumber(), true);
        String refreshToken = jwtProvider.generateToken(dto.getPhoneNumber(), false);

        return ApiResult.successResponse(new CheckUserRegisterDto(accessToken, refreshToken));
    }

    @Override
    public ApiResult<TokenDto> refreshToken(TokenDto dto) {
        try {
            jwtProvider.validateToken(dto.getAccessToken());
            return ApiResult.successResponse(dto);
        } catch (ExpiredJwtException exception) {
            Claims claims = exception.getClaims();
            String subject = claims.getSubject();

            try {
                jwtProvider.validateToken(dto.getRefreshToken());
                String username = jwtProvider.getUsername(dto.getRefreshToken());
                if (!username.equals(subject)) {
                    throw RestException.forbidden();
                }
                String accessToken = jwtProvider.generateToken(username, true);
                String refreshToken = jwtProvider.generateToken(username, false);
                return ApiResult.successResponse(new TokenDto(accessToken, refreshToken));
            } catch (Exception e) {
                throw RestException.forbidden();
            }
        } catch (Exception e) {
            throw RestException.forbidden();
        }

    }

    @Override
    public ApiResult<TokenDto> login(LoginDto loginDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();
            User user = userPrincipal.getUser();
            String accessToken = jwtProvider.generateToken(user.getPhoneNumber(), true);
            String refreshToken = jwtProvider.generateToken(user.getPhoneNumber(), false);
            return ApiResult.successResponse(new TokenDto(accessToken, refreshToken));
        } catch (Exception e) {
            e.printStackTrace();
            throw RestException.restThrow(MessageService.getMessage("WRONG_USERNAME_OR_PASSWORD"), HttpStatus.FORBIDDEN);
        }
    }

    public String generateCode() {
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            verificationCode.append((int) (Math.random() * 10));
        }
        return verificationCode.toString();
    }

}
