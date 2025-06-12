package com.tom.service.knowledges.tag;

import jakarta.validation.constraints.NotBlank;

public record RenameTagRequest(
		
		@NotBlank(message = "Current name cannot be null")
		String currentName, 
		
		@NotBlank(message = "New name cannot be null")
		String newName
		
	) {

}
