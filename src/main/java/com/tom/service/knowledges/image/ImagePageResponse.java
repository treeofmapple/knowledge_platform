package com.tom.service.knowledges.image;

import java.util.List;

public record ImagePageResponse(

	List<Image> content,
	int page,
	int size,
	long totalPages
		
) {

}
