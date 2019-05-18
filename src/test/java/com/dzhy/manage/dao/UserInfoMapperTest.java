package com.dzhy.manage.dao;

import com.dzhy.manage.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName UserInfoMapperTest
 * @Description TODO
 * @Author alex
 * @Date 2019-05-18
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserInfoMapperTest {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    public void insertTest() {
        UserInfo userInfo = UserInfo.builder()
                .username("test")
                .name("zjc")
                .password("$2a$10$sw34isaLbnsQjJAkGmB03uDNrWFad/Hy5N2Z4kTnIpx9MdVnq9yEi")
                .roles("SYS_ADMIN")
                .status(1)
                .build();
        int count = userInfoMapper.insertSelective(userInfo);
        System.out.println(count);
    }

    @Test
    public void select() {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(null);
        System.out.println(userInfo);
    }
}
