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

import com.tom.service.knowledges.image.ImageService;
import com.tom.service.knowledges.tag.TagService;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = {TagService.class, ImageService.class})
public interface NoteMapper {

	NoteMapper INSTANCE = Mappers.getMapper(NoteMapper.class);
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "attachments", ignore = true)
	// I will transform all the String into a byte then store it 
	@Mapping(target = "annotation", expression = "java(request.annotation().getBytes(java.nio.charset.StandardCharsets.UTF_8))")
    @Mapping(source = "tags", target = "tags")
    @Mapping(source = "image", target = "image")
	Note build(CreateNoteRequest request);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "attachments", ignore = true)
	@Mapping(target = "annotation", expression = "java(request.annotation().getBytes(java.nio.charset.StandardCharsets.UTF_8))")
	@Mapping(source = "tags", target = "tags")
	@Mapping(source = "image", target = "image")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateNoteFromRequest(@MappingTarget Note note, EditNoteRequest request);
	
    @Mapping(source = "annotation", target = "annotation", expression = "java(note.getAnnotation() != null ? new String(note.getAnnotation(), java.nio.charset.StandardCharsets.UTF_8) : null)")
    NoteResponse toResponse(Note note);
	
	List<NoteResponse> toResponseList(List<Note> notes);
	
	default NotePageResponse toNotePageResponse(Page<Note> page) {
		List<NoteResponse> content = toResponseList(page.getContent());
		return new NotePageResponse(content,
				page.getNumber(),
				page.getSize(),
				page.getTotalPages(),
				page.getTotalElements());
	}
	
}
