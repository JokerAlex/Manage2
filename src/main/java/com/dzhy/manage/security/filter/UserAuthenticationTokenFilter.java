package com.dzhy.manage.security.filter;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName UserAuthenticationTokenFilter
 * @Description AuthenticationTokenFilter
 * @Author alex
 * @Date 2019-05-18
 **/
@Component
@Slf4j
public class UserAuthenticationTokenFilter extends OncePerRequestFilter {


    private final UserDetailsService iUserDetailsService;

    @Autowired
    public UserAuthenticationTokenFilter(UserDetailsService iUserDetailsService) {
        this.iUserDetailsService = iUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = "Authorization";
        String header = request.getHeader(tokenHeader);

        String tokenHead = "Bearer ";
        if (header != null && header.startsWith(tokenHead)) {
            String username = "";
            try {
                String token = header.substring(tokenHead.length());
                Claims claims = JwtUtil.parseJwt(token);
                username = claims.get("username", String.class);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            if (StringUtils.isNotBlank(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = iUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user = {}, setting security context", userDetails.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
