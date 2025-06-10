package com.tom.service.knowledges.notes;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record EditNoteRequest(
		
	    @NotBlank(message = "Note name cannot be blank.")
	    String name,
		String description,
		
	    @Length(max = 50000)
	    String annotation,
	    Long imageId,
	    Set<String> tags
	    
	) {

}
