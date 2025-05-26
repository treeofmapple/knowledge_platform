package com.tom.service.knowledges.attachments;

import java.util.List;

public record AttachmentPageResponse(

	List<Attachment> content,
	int page,
	int size,
	long totalPages
		
) {

}
