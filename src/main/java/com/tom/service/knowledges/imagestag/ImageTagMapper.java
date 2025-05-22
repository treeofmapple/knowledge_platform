package com.tom.service.knowledges.imagestag;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageTagMapper {

	ImageTagMapper INSTANCE = Mappers.getMapper(ImageTagMapper.class);
	
	// @Mapping(source = "", target = "")
	@Mapping(source = "image.name", target = "imageName")
	@Mapping(source = "image.size", target = "imageSize")
	@Mapping(source = "category", target = "category")
	@Mapping(source = "subCategory", target = "subCategory")
	ImageTagResponse fromTagResponse(ImageTag imageTag);
	
	@Mapping(source = "category", target = "category")
	@Mapping(source = "subCategory", target = "subCategory")
	ItemTagResponse fromItemResponse(ImageTag imageTag);
	
}
