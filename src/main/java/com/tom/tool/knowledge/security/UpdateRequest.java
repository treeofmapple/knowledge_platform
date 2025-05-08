package com.tom.tool.knowledge.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRequest(
		
	    @Size(min = 4, message = "Username must be at least 4 characters")
	    String username,

	    @Email(message = "Email must be valid")
	    String email,

	    @Min(value = 13, message = "Age must be at least 13")
	    int age,

	    @NotBlank(message = "Password must not be blank")
	    String password,

	    @NotBlank(message = "Confirm password must not be blank")
	    String confirmPassword
		
) {

}
