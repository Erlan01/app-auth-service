package ai.ecma.auth.controller;

import ai.ecma.auth.annotation.CurrentUser;
import ai.ecma.auth.payload.AddStaffDto;
import ai.ecma.auth.payload.EditUserDto;
import ai.ecma.auth.payload.UserPrincipal;
import ai.ecma.auth.payload.UserReqDto;
import ai.ecma.auth.utils.AppConstant;
import ai.ecma.lib.entity.User;
import ai.ecma.lib.payload.ApiResult;
import ai.ecma.lib.payload.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(UserController.USER_CONTROLLER)
public interface UserController {

    String USER_CONTROLLER = AppConstant.BASE_PATH + "/user";

    @GetMapping
    ApiResult<UserDto> me(@CurrentUser UserPrincipal userPrincipal);

    @PostMapping("/check-auth")
    ApiResult<UserDto> checkAuth(@CurrentUser UserPrincipal userPrincipal);
    // edit

    @PutMapping("/{id}")
    ApiResult<?> edit(@PathVariable UUID id, @RequestBody UserReqDto userReqDto);


    @PutMapping("/edit")
    ApiResult<EditUserDto> editUser(@CurrentUser User user, @RequestBody EditUserDto dto);

    @PostMapping("/staff")
    @PreAuthorize("hasAnyAuthority('ADD_STAFF')")
    ApiResult<?> addStaff(@RequestBody @Valid AddStaffDto staffDto);

}

