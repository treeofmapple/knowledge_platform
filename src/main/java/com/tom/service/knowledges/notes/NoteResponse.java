package com.tom.service.knowledges.notes;

import java.time.LocalDateTime;
import java.util.Set;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.image.Image;
import com.tom.service.knowledges.tag.TagResponse;

public record NoteResponse(
		
		String name,
		String description,
		String annotation,
		Image image,
		Boolean notePrivated,
		Set<Attachment> attachments,
		Set<TagResponse> tags,
		LocalDateTime createdAt
		
		) {

}
