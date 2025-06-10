package com.tom.service.knowledges.notes;

import java.util.Set;

import org.hibernate.validator.constraints.Length;

public record EditNoteRequest(
		
	    String name,
	    
		String description,
		
	    @Length(max = 50000)
	    String annotation,
	    
	    Long imageId,
	    Set<String> tags
	    
	) {

}
