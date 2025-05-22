package com.tom.service.knowledges.image;

import java.time.LocalDateTime;
import java.util.List;

public record ImageResponse(
		
	String name,
	String contentType,
	Long size,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<TagDTO> tags
		
) {

	public record TagDTO(String tagKey, String tagValue) {}
	
}
