package ai.ecma.auth.controller;

import ai.ecma.auth.exception.RestException;
import ai.ecma.auth.payload.*;
import ai.ecma.auth.utils.AppConstant;
import ai.ecma.lib.payload.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping(AuthController.AUTH_CONTROLLER)
public interface AuthController {

    String AUTH_CONTROLLER = AppConstant.BASE_PATH + "/auth";

    @PostMapping("/check-phone")
    ApiResult<?> checkPhone(@RequestBody @Valid CheckPhoneDto dto);

    @PostMapping("/verify")
    ApiResult<?> verify(@RequestBody @Valid PhoneVerifyDto dto);

    @PostMapping("/sign-up")
    ApiResult<?> signUp(@RequestBody @Valid RegisterDto dto);

    @PostMapping("/refresh-token")
    ApiResult<TokenDto> refreshToken(@RequestBody @Valid TokenDto dto);

    @PostMapping("/login")
    ApiResult<TokenDto> login(@RequestBody @Valid LoginDto loginDto);

    @GetMapping
    ApiResult<?> test();


}

