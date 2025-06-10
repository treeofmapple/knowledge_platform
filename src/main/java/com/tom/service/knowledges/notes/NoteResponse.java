package com.tom.service.knowledges.notes;

import java.time.LocalDateTime;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.image.Image;
import com.tom.service.knowledges.tag.Tag;

public record NoteResponse(
		
		String name,
		
		String description,
		
		String annotation,
		
		Image image,
		
		Attachment attachments,
		
		Tag tags,
		
		LocalDateTime createdAt
		
		) {

}
