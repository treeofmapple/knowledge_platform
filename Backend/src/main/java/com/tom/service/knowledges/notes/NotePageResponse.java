package com.tom.service.knowledges.notes;

import java.util.List;

public record NotePageResponse(
		
	List<NoteResponse> content,
	int page,
	int size,
	long totalPages,
	long totalElements
		
	) {

}
