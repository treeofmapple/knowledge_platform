package com.tom.service.knowledges.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final SystemUtils utils;

    @Value("${application.security.secret-key}")
    private String secretKey;

	@Value("${application.security.expiration}")
	private String jwtExpiration;

	@Value("${application.security.refresh-token.expiration}")
	private String refreshExpiration;

	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}
	
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails ) {
		return buildToken(extraClaims, userDetails, utils.parseDuration(jwtExpiration));
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return buildToken(new HashMap<>(), userDetails, utils.parseDuration(refreshExpiration));
	}
	
	public boolean isTokenValid(String token, UserDetails userDetails){
		final String username = extractUsername(token);
		return (username.equals(getIdentifier(userDetails))) && !isTokenExpired(token);
	}	
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private String getIdentifier(UserDetails userDetails) {
	    if (userDetails instanceof User user) {
	        return user.getIdentifier();
	    }
	    return userDetails.getUsername();
	}
	
	private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
		String subject = (userDetails instanceof User user) ? user.getIdentifier() : userDetails.getUsername();
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
}
