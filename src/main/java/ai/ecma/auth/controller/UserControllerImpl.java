package ai.ecma.auth.controller;

import ai.ecma.auth.payload.AddStaffDto;
import ai.ecma.auth.payload.EditUserDto;
import ai.ecma.auth.payload.UserPrincipal;
import ai.ecma.auth.payload.UserReqDto;
import ai.ecma.auth.service.UserService;
import ai.ecma.lib.entity.User;
import ai.ecma.lib.payload.ApiResult;
import ai.ecma.lib.payload.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * This class not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 21.01.2022
 */
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {
    private final UserService userService;

    @Override
    public ApiResult<UserDto> me(UserPrincipal userPrincipal) {
        return userService.me(userPrincipal);
    }

    @Override
    public ApiResult<UserDto> checkAuth(UserPrincipal userPrincipal) {
        return userService.checkAuth(userPrincipal);
    }

    @Override
    public ApiResult<?> edit(UUID id, UserReqDto userReqDto) {
        return userService.edit(id, userReqDto);
    }

    @Override
    public ApiResult<EditUserDto> editUser(User user, EditUserDto dto) {
        return userService.editUser(user, dto);
    }

    @Override
    public ApiResult<?> addStaff(AddStaffDto staffDto) {
        return userService.addStaff(staffDto);
    }
}
