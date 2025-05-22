package com.tom.service.knowledges.security;

public record AuthenticationResponse(

		String accessToken,

		String refreshToken
		
) {
}
