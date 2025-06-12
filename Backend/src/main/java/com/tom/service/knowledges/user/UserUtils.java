package com.tom.service.knowledges.user;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.security.AuthenticationMapper;
import com.tom.service.knowledges.security.TokenRepository;
import com.tom.service.knowledges.security.TokenType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUtils {

	private final TokenRepository tokenRepository;
	private final AuthenticationMapper mapper;
	
	public void saveUserToken(User user, String jwtToken) {
		var token = mapper.buildToken(user, jwtToken, TokenType.BEARER, false, false);
		tokenRepository.save(token);
	}

	public void revokeAllUserTokens(User user) {
		var validUser = tokenRepository.findAllValidTokenByUser(user.getId());
		if (validUser.isEmpty()) {
			return;
		}
		validUser.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUser);
	}
	
}
