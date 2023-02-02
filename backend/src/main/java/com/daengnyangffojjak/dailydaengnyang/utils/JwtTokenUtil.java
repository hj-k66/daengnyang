package com.daengnyangffojjak.dailydaengnyang.utils;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.token.TokenInfo;
import com.daengnyangffojjak.dailydaengnyang.exception.ErrorCode;
import com.daengnyangffojjak.dailydaengnyang.exception.SecurityCustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenUtil {

	private final UserDetailsService userDetailsService;

	@Value("${jwt.token.secret}")
	private String secretKey;

	private final static long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; //1시간
	private final static long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; //7일


	//secretKey는 256bit보다 커야 한다. 영어 한단어당 8bit 이므로 32글자 이상이어야 한다는 뜻이다.
	private Key makeKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
	}

	public TokenInfo createToken(Authentication authentication) {
		String userName = authentication.getName();
		List<String> authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		//access token 생성
		String accessToken = generateAccessToken(userName, authorities);

		//Refresh Token 생성
		String refreshToken = generateRefreshToken();

		return TokenInfo.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.refreshTokenExpireTime(REFRESH_TOKEN_EXPIRE_TIME)
				.build();
	}

	public String generateAccessToken(String userName, List<String> authorities) {
		//Access Token 생성
		Claims claims = Jwts.claims();
		claims.put("userName", userName);
		claims.put("role", authorities);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(
						new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME)) //토큰 만료 시간
				.signWith(makeKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String generateRefreshToken() {

		return Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
				.signWith(makeKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public String getUserName(String token) {
		return extractClaims(token).get("userName", String.class);
	}

	public boolean validateToken(String token) {
		try {
			extractClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	private Claims extractClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(makeKey()).build().parseClaimsJws(token)
					.getBody();
		} catch (IllegalArgumentException e) {
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN);
		} catch (
				ExpiredJwtException e) {
			log.info("만료된 토큰입니다.");
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN, "토큰 기한 만료");
		} catch (
				SignatureException e) {
			log.info("서명이 일치하지 않습니다.");
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN, "서명 불일치");
		}
	}

	public UserDetails getUserDetails(String token) {
		String userName = getUserName(token);
		//UserName Token에서 꺼내기
		log.info("userName : {}", userName);
		return userDetailsService.loadUserByUsername(userName);
	}

	//만료된 accesstoken에서도 보유한 사용자 정보를 이용하여 Authentication을 생성하기 위해 parse 따로 함
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		String userName = claims.get("userName", String.class);
		UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
		return new UsernamePasswordAuthenticationToken(userDetails, "",
				userDetails.getAuthorities());
	}

	private Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(makeKey()).build().parseClaimsJws(accessToken)
					.getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		} catch (SignatureException e) {
			log.info("서명이 일치하지 않습니다.");
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN, "서명 불일치");
		} catch (IllegalArgumentException e) {
			throw new SecurityCustomException(ErrorCode.INVALID_TOKEN);
		}
	}

}
