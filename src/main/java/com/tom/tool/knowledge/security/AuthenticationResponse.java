package com.tom.tool.knowledge.security;

public record AuthenticationResponse(

		String accessToken,

		String refreshToken
		
) {
}
