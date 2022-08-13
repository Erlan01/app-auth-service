package ai.ecma.auth.service;

import ai.ecma.auth.payload.AddStaffDto;
import ai.ecma.auth.payload.EditUserDto;
import ai.ecma.auth.payload.UserPrincipal;
import ai.ecma.auth.payload.UserReqDto;
import ai.ecma.lib.entity.User;
import ai.ecma.lib.payload.ApiResult;
import ai.ecma.lib.payload.UserDto;

import java.util.UUID;

/**
 * This interface not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 21.01.2022
 */
public interface UserService {
    ApiResult<UserDto> me(UserPrincipal userPrincipal);

    ApiResult<UserDto> checkAuth(UserPrincipal userPrincipal);

    ApiResult<?> edit(UUID id, UserReqDto userReqDto);

    ApiResult<EditUserDto> editUser(User user, EditUserDto dto);

    ApiResult<?> addStaff(AddStaffDto staffDto);

}
