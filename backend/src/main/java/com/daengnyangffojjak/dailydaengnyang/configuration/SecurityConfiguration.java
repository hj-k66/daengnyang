package com.daengnyangffojjak.dailydaengnyang.configuration;


import com.daengnyangffojjak.dailydaengnyang.security.CustomAccessDeniedHandler;
import com.daengnyangffojjak.dailydaengnyang.security.CustomAuthenticationEntryPoint;
import com.daengnyangffojjak.dailydaengnyang.security.CustomOAuth2Service;
import com.daengnyangffojjak.dailydaengnyang.security.JwtExceptionFilter;
import com.daengnyangffojjak.dailydaengnyang.security.JwtTokenFilter;
import com.daengnyangffojjak.dailydaengnyang.security.OAuth2AuthenticationSuccessHandler;
import com.daengnyangffojjak.dailydaengnyang.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtTokenUtil jwtTokenUtil;
	private final RedisTemplate redisTemplate;
	private final CustomOAuth2Service customOAuth2Service;
	private final OAuth2AuthenticationSuccessHandler OAuth2AuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
				.httpBasic().disable()
				.csrf().disable()
				//springboot 3.0부터 security HTTP 요청 권한 승인 로직 변경
				//Instead of using authorizeRequests, use authorizeHttpRequests
				.cors().and().authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/**").permitAll()
						.requestMatchers("/view/**").permitAll()
						.requestMatchers("/utils/profile").permitAll()
						.requestMatchers("/actuator/health").permitAll()
						.requestMatchers("/api/v1/users/**").permitAll()
						.requestMatchers("/api/v1/users/join", "/api/v1/users/login").permitAll()
						.requestMatchers("/docs/index.html").permitAll()
						.requestMatchers("/oauth2/authorization/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()
						.requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
				)
				.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
				.and()
				.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt사용하는 경우 씀
				.and()
				.oauth2Login()
				.userInfoEndpoint()
				.userService(customOAuth2Service)    //provider로부터 획득한 유저정보를 다룰 service단을 지정한다.
				.and()
				.successHandler(OAuth2AuthenticationSuccessHandler)
//				.failureHandler(authenticationFailureHandler)
				.and()
				.addFilterBefore(new JwtTokenFilter(jwtTokenUtil, redisTemplate),
						UsernamePasswordAuthenticationFilter.class) //UserNamePasswordAuthenticationFilter적용하기 전에 JWTTokenFilter를 적용
				.addFilterBefore(new JwtExceptionFilter(), JwtTokenFilter.class)
				.build();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			private final long MAX_AGE_SECS = 3600;

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("https://daengnyang.site")
						.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(MAX_AGE_SECS);
			}
		};
	}
}

