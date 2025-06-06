package com.tom.service.knowledges.attachments;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

	AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

	// @Mapping(source = "", target = "")
	AttachmentResponse fromResponse(Attachment image);

	List<AttachmentResponse> toResponseList(List<Attachment> attachments);

}
