package com.dzhy.manage.config;

import com.dzhy.manage.interceptor.SignInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName WebConfig
 * @Description web config
 * @Author alex
 * @Date 2019-05-28
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SignInterceptor signInterceptor;

    @Autowired
    public WebConfig(SignInterceptor signInterceptor) {
        this.signInterceptor = signInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(signInterceptor).addPathPatterns("/**");
    }
}
