package com.tom.service.knowledges.attachments;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

	AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);
	
	// @Mapping(source = "", target = "")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "contentType", target = "contentType")
	@Mapping(source = "size", target = "size")
	@Mapping(source = "createdAt", target = "createdAt")
	@Mapping(source = "updatedAt", target = "updatedAt")
	AttachmentResponse fromResponse(Attachment image);

	AttachmentPageResponse fromPageResponse(List<AttachmentResponse> imagePage, int page, int size, int totalPages);

}
