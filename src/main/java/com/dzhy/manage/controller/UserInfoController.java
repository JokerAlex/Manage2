package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName UserInfoController
 * @Description UserInfoController
 * @Author alex
 * @Date 2019-05-18
 **/
@RestController
@RequestMapping("/user")
@Api(value = "用户信息", description = "用户信息管理")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Autowired
    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @ApiOperation(value = "检查用户名", notes = "检查用户名是否可用")
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataTypeClass = String.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @GetMapping("/check")
    public Result checkUsername(@RequestParam(value = "username") String username) {
        UserInfo userInfo = userInfoService.checkUsername(username);
        if (userInfo == null) {
            return Result.isSuccess();
        }
        return Result.isError();
    }

    @ApiOperation(value = "添加", notes = "添加新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roles", value = "角色", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "status", value = "账号状态，0-禁用，1-可用", required = true, dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @PostMapping()
    public Result addUserInfo(@RequestParam(value = "username") String username,
                              @RequestParam(value = "password") String password,
                              @RequestParam(value = "name") String name,
                              @RequestParam(value = "roles") String roles,
                              @RequestParam(value = "status", defaultValue = "1") int status) {
        UserInfo userInfo = UserInfo.builder()
                .username(username)
                .password(password)
                .name(name)
                .roles(roles)
                .status(status)
                .build();
        return userInfoService.insertUserInfo(userInfo);
    }

    @ApiOperation(value = "用户更新", notes = "更新用户用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "roles", value = "角色", dataTypeClass = String.class),
            @ApiImplicitParam(name = "status", value = "账号状态，0-禁用，1-可用", dataTypeClass = Integer.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping()
    public Result updateUserInfoOther(@RequestParam(value = "userId") Integer userId,
                                      @RequestParam(value = "name") String name,
                                      @RequestParam(value = "roles", required = false) String roles,
                                      @RequestParam(value = "status", required = false, defaultValue = "1") int status) {
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .name(name)
                .roles(roles)
                .status(status)
                .build();
        return userInfoService.updateUserInfo(userInfo);
    }

    @ApiOperation(value = "修改密码", notes = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPass", value = "原密码", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "newPass", value = "新密码", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping("/pass")
    public Result changePassword(@RequestParam(value = "oldPass") String oldPass,
                                 @RequestParam(value = "newPass") String newPass) {
        return userInfoService.updatePassword(oldPass, newPass);
    }

    @ApiOperation(value = "重置密码", notes = "重置密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pass", value = "新密码", required = true, dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @PostMapping("/reset")
    public Result resetPassword(@RequestParam(value = "userId") Integer userId,
                                @RequestParam(value = "pass") String pass) {
        return userInfoService.resetPassword(userId, pass);
    }

    @ApiOperation(value = "删除", notes = "删除用户")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @DeleteMapping()
    public Result deleteUserInfo(@RequestParam(value = "userIds[]") List<Integer> userIds) {
        return userInfoService.deleteUserInfo(userIds);
    }

    @ApiOperation(value = "列表", notes = "获取用户信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", value = "用户真实名称，模糊查询使用", dataTypeClass = String.class)
    })
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @GetMapping()
    public Result listUserInfo(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "name", required = false) String name) {
        return userInfoService.listUserInfo(pageNum, pageSize, name);
    }
}
