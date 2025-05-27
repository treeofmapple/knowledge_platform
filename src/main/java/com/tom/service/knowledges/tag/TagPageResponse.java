package com.tom.service.knowledges.tag;

import java.util.List;

public record TagPageResponse(
		
	List<TagResponse> content,
	int page,
	int size,
	long totalPages,
	long totalElements
	
	) {

}
