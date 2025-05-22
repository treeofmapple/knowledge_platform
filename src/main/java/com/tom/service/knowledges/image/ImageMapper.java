package com.tom.service.knowledges.image;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

	ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);
	
	// @Mapping(source = "", target = "")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "contentType", target = "contentType")
	@Mapping(source = "size", target = "size")
	@Mapping(source = "createdAt", target = "createdAt")
	@Mapping(source = "updatedAt", target = "updatedAt")
	ImageResponse fromImage(Image image);

	ImagePageResponse fromPage(List<ImageResponse> imagePage, int page, int size, int totalPages);
}
