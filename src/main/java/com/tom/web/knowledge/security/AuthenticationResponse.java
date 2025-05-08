package com.tom.web.knowledge.security;

public record AuthenticationResponse(

		String accessToken,

		String refreshToken
		
) {
}
