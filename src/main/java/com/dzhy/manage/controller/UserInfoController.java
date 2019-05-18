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
    @ApiImplicitParam(name = "userInfo", value = "用户实体类", required = true, dataTypeClass = UserInfo.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @PostMapping()
    public Result addUserInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.insertUserInfo(userInfo);
    }

    @ApiOperation(value = "用户更新", notes = "更新用户用户信息")
    @ApiImplicitParam(name = "userInfo", value = "用户实体类", required = true, dataTypeClass = UserInfo.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN', 'OPERATOR', 'UESER')")
    @PutMapping()
    public Result updateUserInfoOther(@RequestBody UserInfo userInfo) {
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
    @ApiImplicitParam(name = "pass", value = "新密码", required = true, dataTypeClass = String.class)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'ADMIN')")
    @PostMapping("/reset")
    public Result resetPassword(@RequestParam(value = "pass") String pass) {
        return userInfoService.resetPassword(pass);
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
