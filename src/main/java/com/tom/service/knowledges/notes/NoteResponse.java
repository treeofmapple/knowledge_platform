package com.tom.service.knowledges.notes;

import java.time.LocalDateTime;
import java.util.Set;

import com.tom.service.knowledges.attachments.AttachmentResponse;
import com.tom.service.knowledges.image.ImageResponse;
import com.tom.service.knowledges.tag.TagResponse;

public record NoteResponse(
		
		String name,
		String description,
		String annotation,
		ImageResponse image,
		Boolean notePrivated,
		Set<AttachmentResponse> attachments,
		Set<TagResponse> tags,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
		
		) {

}
