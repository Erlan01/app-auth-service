package ai.ecma.auth.service;

import ai.ecma.auth.payload.*;
import ai.ecma.lib.payload.ApiResult;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This interface not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 19.01.2022
 */
public interface AuthService extends UserDetailsService {
    ApiResult<?> checkPhone(CheckPhoneDto dto);

    ApiResult<?> verify(PhoneVerifyDto dto);

    ApiResult<?> signUp(RegisterDto dto);

    ApiResult<TokenDto> refreshToken(TokenDto dto);

    ApiResult<TokenDto> login(LoginDto loginDto);
}
