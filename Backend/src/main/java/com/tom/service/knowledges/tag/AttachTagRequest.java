package com.tom.service.knowledges.tag;

import jakarta.validation.constraints.NotBlank;

public record AttachTagRequest(
		

	    @NotBlank(message = "Note name cannot be blank")
	    String noteName,

	    @NotBlank(message = "Tag name cannot be blank")
	    String tagName
		
		
) {

}
