package com.tom.service.knowledges.image;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.tom.aws.awstest.common.ServiceLogger;
import com.tom.aws.awstest.exception.ConflictException;
import com.tom.aws.awstest.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUtils {

	private final ImageRepository repository;
	private final ImageMapper mapper;
	
	public List<Image> findObjectList() {
		List<Image> image = repository.findAll();
		if(image.isEmpty()) {
			String message = "No products found in the database";
			ServiceLogger.warn(message);
			throw new NotFoundException(message);
		}
		return image;
	}
	
	public List<ImageResponse> findObjectListPaged(Page<Image> imagePage) {
		List<ImageResponse> content = imagePage
				.stream()
				.map(mapper::fromImage)
				.collect(Collectors.toList());
		return content;
	}
	
	public Image findObject(String name) {
		return repository.findByName(name).orElseThrow(() -> {
            String message = String.format("Image with name %s not found", name);
            ServiceLogger.error(message);
            return new NotFoundException(message);
		});
	}
	
	public void ensureObjectExist(String name) {
		if (repository.existsByName(name)) {
			String message = "Object already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}

	public void ensureObjectNotExist(String name) {
		if (!repository.existsByName(name)) {
			String message = "Object with name " + name + " not exists.";
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
}
