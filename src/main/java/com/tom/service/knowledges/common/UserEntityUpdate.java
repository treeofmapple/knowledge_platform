package com.tom.service.knowledges.common;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.exception.NotFoundException;
import com.tom.service.knowledges.security.AuthenticationMapper;
import com.tom.service.knowledges.security.TokenRepository;
import com.tom.service.knowledges.security.TokenType;
import com.tom.service.knowledges.security.UpdateRequest;
import com.tom.service.knowledges.security.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEntityUpdate {

	private final TokenRepository tokenRepository;
	private final AuthenticationMapper mapper;
	
	public User mergeData(User user, UpdateRequest request) {
		user.setUsername(request.username());
		user.setEmail(request.email());
		user.setAge(request.age());
		return user;
	}
	
	public void saveUserToken(User user, String jwtToken) {
		var token = mapper.buildAttributes(user, jwtToken, TokenType.BEARER, false, false);
		tokenRepository.save(token);
	}

	public void revokeAllUserTokens(User user) {
		var validUser = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUser.isEmpty()) {
			throw new NotFoundException("No active tokens found for user");
		}
		validUser.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUser);
	}
	
}
