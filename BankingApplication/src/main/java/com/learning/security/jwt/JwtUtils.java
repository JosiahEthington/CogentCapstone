package com.learning.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.learning.security.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${com.learning.jwtSecret}")
	private String jwtSecret;
	@Value("${com.learning.jwtExpirationMs}")
	private Long jwtExpirationMs;
	
	public String generateToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime()+jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}
	
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());

		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());

		} catch (SignatureException e) {
			logger.error("JWT token signature cannot be verified: {}", e.getMessage());

		} catch (IllegalArgumentException e) {
			logger.error("Illegal argument: {}", e.getMessage());

		}
		return false;
	}
	
	public String getUsernameFromJwtToken(String authToken) {
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(authToken)
				.getBody()
				.getSubject();
	}
}
