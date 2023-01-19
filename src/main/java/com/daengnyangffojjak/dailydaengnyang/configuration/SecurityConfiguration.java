package com.daengnyangffojjak.dailydaengnyang.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final UserService userService;
    @Value("${jwt.token.secret}")
    private String secretKey;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                //springboot 3.0부터 security HTTP 요청 권한 승인 로직 변경
                //Instead of using authorizeRequests, use authorizeHttpRequests
                .cors().and().authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/users/join", "/api/v1/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/v1/**").authenticated()
                        .requestMatchers(HttpMethod.PUT,"/api/v1/**").authenticated()
                )
                //CustomAccessDeniedHadler,CustomAuthenticationEntryPoint 차후 추가
//                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHadler())
//                .and()
//                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt사용하는 경우 씀
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class) //UserNamePasswordAuthenticationFilter적용하기 전에 JWTTokenFilter를 적용
                .addFilterBefore(new JwtExceptionFilter(), JwtTokenFilter.class)
                .build();
    }
}

