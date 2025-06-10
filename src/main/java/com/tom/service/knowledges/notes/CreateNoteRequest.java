package com.tom.service.knowledges.notes;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateNoteRequest(

	    @NotBlank(message = "Note name cannot be blank.")
	    String name,
	    
	    String description,

	    @NotBlank(message = "Annotation cannot be blank.")
	    @Length(max = 50000)
	    String annotation,

	    Long imageId,
	    Set<String> tags

) {

}
