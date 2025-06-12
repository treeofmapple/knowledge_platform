package com.tom.service.knowledges.attachments;

import java.time.LocalDateTime;

public record AttachmentResponse(
		
	String name,
    String objectUrl,
	String contentType,
	Long size,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
		
) {

}
