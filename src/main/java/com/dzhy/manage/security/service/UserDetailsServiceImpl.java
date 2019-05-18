package com.dzhy.manage.security.service;

import com.dzhy.manage.dao.UserInfoMapper;
import com.dzhy.manage.security.entity.JwtUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @ClassName UserDetailsServiceImpl
 * @Description UserDetails
 * @Author alex
 * @Date 2019-05-18
 **/
@Service("iUserDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserInfoMapper userInfoMapper;

    @Autowired
    public UserDetailsServiceImpl(UserInfoMapper userInfoMapper) {
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return Optional
                .ofNullable(userInfoMapper.selectByUsername(userName))
                .map(JwtUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + userName));
    }
}
