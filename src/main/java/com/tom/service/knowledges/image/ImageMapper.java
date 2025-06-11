package com.tom.service.knowledges.image;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

	ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);
	
	@Mapping(source = "updatedAt", target = "updatedAt")
	ImageResponse toResponse(Image image);
	
    @Mapping(source = "file.originalFilename", target = "name")
    @Mapping(source = "file.contentType", target = "contentType")
    @Mapping(source = "file.size", target = "size")
    @Mapping(source = "key", target = "objectKey")
    @Mapping(source = "url", target = "objectUrl")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeFromMultipartFile(@MappingTarget Image Images, MultipartFile file, String key, String url);

}
