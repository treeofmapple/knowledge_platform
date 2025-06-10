package com.tom.service.knowledges.attachments;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

	AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

	// @Mapping(source = "", target = "")
	AttachmentResponse toResponse(Attachment image);

	List<AttachmentResponse> toResponseList(List<Attachment> attachments);

    @Mapping(source = "file.originalFilename", target = "name")
    @Mapping(source = "file.contentType", target = "contentType")
    @Mapping(source = "file.size", target = "size")
    @Mapping(source = "key", target = "objectKey")
    @Mapping(source = "url", target = "objectUrl")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeFromMultipartFile(@MappingTarget Attachment attachment, MultipartFile file, String key, String url);

    @Mapping(source = "key", target = "objectKey")
    @Mapping(source = "url", target = "objectUrl")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeFromKeyAndUrl(@MappingTarget Attachment attachment, String key, String url);
}
