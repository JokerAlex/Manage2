package com.dzhy.manage.service;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.constants.Constants;
import com.dzhy.manage.dao.UserInfoMapper;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.enums.ResultEnum;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.utils.CommonUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName UserInfoService
 * @Description 用户信息 service
 * @Author alex
 * @Date 2019-05-17
 **/
@Service
@Slf4j
public class UserInfoService {

    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoService(UserInfoMapper userInfoMapper, PasswordEncoder passwordEncoder) {
        this.userInfoMapper = userInfoMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfo checkUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        return userInfoMapper.selectByUsername(username);
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result insertUserInfo(UserInfo userInfo) throws GeneralException {
        if (userInfo == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        UserInfo origin = this.checkUsername(userInfo.getUsername());
        if (origin != null) {
            return Result.isError("用户名已存在");
        }

        UserInfo user = UserInfo.builder()
                .username(userInfo.getUsername())
                .password(passwordEncoder.encode(userInfo.getPassword()))
                .name(userInfo.getName())
                .roles(userInfo.getRoles().toUpperCase())
                .status(1)
                .build();
        try {
            userInfoMapper.insertSelective(user);
            log.info("add userInfo success userId = {}", user.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.ADD_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result updateUserInfo(UserInfo userInfo) throws GeneralException {
        if (userInfo == null || userInfo.getUserId() == null) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        UserInfo userSource = userInfoMapper.selectByPrimaryKey(CommonUtil.getUserIdFromContext());
        if (userSource == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage() + "-ID:" + userInfo.getUserId());
        }
        String roles = userSource.getRoles();
        if (roles.contains(Constants.SYS_ADMIN) || roles.contains(Constants.ADMIN)) {
            userSource.setRoles(userInfo.getRoles().toUpperCase());
            userSource.setStatus(userInfo.getStatus());
        }
        userSource.setName(userInfo.getName());
        userSource.setUpdateTime(null);
        try {
            userInfoMapper.updateByPrimaryKeySelective(userSource);
            log.info("update userInfo success userId = {}", userSource.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.UPDATE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result resetPassword(String pass) throws GeneralException {
        if (StringUtils.isBlank(pass)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(CommonUtil.getUserIdFromContext());
        if (userInfo == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage());
        }
        userInfo.setPassword(passwordEncoder.encode(pass));
        userInfo.setUpdateTime(null);
        try {
            userInfoMapper.updateByPrimaryKeySelective(userInfo);
            log.info("reset password success userId = {}", userInfo.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException("重置密码失败");
        }
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result updatePassword(String oldPass, String newPass) throws GeneralException {
        if (StringUtils.isBlank(oldPass) || StringUtils.isBlank(newPass)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }

        UserInfo userInfoSource = userInfoMapper.selectByPrimaryKey(CommonUtil.getUserIdFromContext());
        if (userInfoSource == null) {
            return Result.isError(ResultEnum.NOT_FOUND.getMessage());
        }

        if (!passwordEncoder.matches(oldPass, userInfoSource.getPassword())) {
            return Result.isError("原密码错误");
        }
        if (oldPass.equals(newPass)) {
            return Result.isError("新密码不能与原密码相同");
        }
        userInfoSource.setPassword(passwordEncoder.encode(newPass));
        userInfoSource.setUpdateTime(null);
        try {
            userInfoMapper.updateByPrimaryKeySelective(userInfoSource);
            log.info("change password success userId = {}", userInfoSource.getUserId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException("修改密码失败");
        }
        SecurityContextHolder.clearContext();
        return Result.isSuccess();
    }

    @Transactional(rollbackFor = GeneralException.class)
    public Result deleteUserInfo(List<Integer> userIds) throws GeneralException {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new GeneralException(ResultEnum.ILLEGAL_PARAMETER.getMessage());
        }
        try {
            int count = userInfoMapper.deleteByIds(userIds);
            if (count != userIds.size()) {
                throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
            }
            log.info("delete users success userIds = {}", userIds);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GeneralException(ResultEnum.DELETE_ERROR.getMessage());
        }
        return Result.isSuccess();
    }

    public Result listUserInfo(int pageNum, int pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> userInfoList = userInfoMapper.selectAll(name);
        userInfoList = userInfoList.stream()
                .filter(userInfo -> !userInfo.getRoles().contains(Constants.SYS_ADMIN))
                .map(userInfo -> UserInfo.builder()
                        .userId(userInfo.getUserId())
                        .username(userInfo.getUsername())
                        .name(userInfo.getName())
                        .roles(userInfo.getRoles())
                        .status(userInfo.getStatus())
                        .createTime(userInfo.getCreateTime())
                        .updateTime(userInfo.getUpdateTime())
                        .build())
                .collect(Collectors.toList());
        PageInfo<UserInfo> pageInfo = new PageInfo<>(userInfoList);
        return Result.isSuccess(pageInfo);
    }
}
