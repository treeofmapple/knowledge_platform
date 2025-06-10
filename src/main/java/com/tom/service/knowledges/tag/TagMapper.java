package com.tom.service.knowledges.tag;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

	TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);
	
	// @Mapping(source = "", target = "")
	@Mapping(target = "id", ignore = true)
	Tag build(String name);
	
	TagResponse toResponse(Tag tags);
	
    List<TagResponse> toResponseList(List<Tag> tags);
    
    default TagPageResponse toTagPageResponse(Page<Tag> page) {
    	List<TagResponse> content = toResponseList(page.getContent());
        return new TagPageResponse(content, 
        		page.getNumber(), 
        		page.getSize(), 
        		page.getTotalPages(), 
        		page.getTotalElements());
    }
	
    @Mapping(target = "name", source = "name")
    void mergeFromName(@MappingTarget Tag tags, String name);
    
}
