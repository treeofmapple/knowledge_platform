package com.tom.service.knowledges.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordRequest(
		
	    @NotBlank(message = "Current password must not be blank")
	    String currentpassword,

	    @NotBlank(message = "New password must not be blank")
	    @Size(min = 8, message = "New password must be at least 8 characters")
	    String newpassword,

	    @NotBlank(message = "Confirmation password must not be blank")
	    String confirmationpassword
) {
}
