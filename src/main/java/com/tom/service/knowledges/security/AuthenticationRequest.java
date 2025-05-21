package com.tom.web.knowledge.security;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(

		@NotBlank(message = "Username or email must be inserted")
		String userinfo,
		
		@NotBlank(message = "Password must not be blank")
		String password
		
) {
}
