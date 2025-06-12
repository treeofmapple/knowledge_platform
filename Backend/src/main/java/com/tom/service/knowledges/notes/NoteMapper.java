package com.tom.service.knowledges.notes;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import com.tom.service.knowledges.attachments.AttachmentMapper;
import com.tom.service.knowledges.image.ImageMapper;
import com.tom.service.knowledges.image.ImageUtils;
import com.tom.service.knowledges.tag.TagUtils;

@Mapper(componentModel = "spring", 
	unmappedTargetPolicy = ReportingPolicy.IGNORE,
	uses = {TagUtils.class, ImageUtils.class, AttachmentMapper.class, ImageMapper.class})
public interface NoteMapper {

	NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);
	
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "annotation", ignore = true)
    @Mapping(source = "notePrivated", target = "notePrivated")
    @Mapping(source = "imageId", target = "image")
    @Mapping(source = "tags", target = "tags")
	Note build(CreateNoteRequest request);
	
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "annotation", ignore = true)
    @Mapping(source = "imageId", target = "image")
    @Mapping(source = "tags", target = "tags")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNoteFromRequest(@MappingTarget Note note, EditNoteRequest request);
    
    @Mapping(target = "annotation", expression = "java(note.getAnnotation() != null ? new String(note.getAnnotation(), StandardCharsets.UTF_8) : null)")
    @Mapping(source = "note.attachments", target = "attachments") 
    NoteResponse toResponse(Note note);
    
	List<NoteResponse> toResponseList(List<Note> notes);
	
	default NotePageResponse toNotePageResponse(Page<Note> page) {
        if (page == null) {
            return null;
        }
		List<NoteResponse> content = toResponseList(page.getContent());
		return new NotePageResponse(content,
				page.getNumber(),
				page.getSize(),
				page.getTotalPages(),
				page.getTotalElements());
	}
	
}
