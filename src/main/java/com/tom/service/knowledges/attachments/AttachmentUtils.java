package com.tom.service.knowledges.attachments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentUtils {

	private final AttachmentRepository repository;
	private final AttachmentMapper mapper;
	
	public List<AttachmentResponse> findObjectListPaged(Page<Attachment> imagePage) {
		List<AttachmentResponse> content = imagePage
				.stream()
				.map(mapper::fromResponse)
				.collect(Collectors.toList());
		return content;
	}
	
	public Attachment findObject(String name) {
		return repository.findByNameIgnoreCase(name).orElseThrow(() -> {
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
	
	public void mergeData(Attachment images, String name, String key, String url, String contentType, long size) {
		images.setName(name);
		images.setObjectKey(key);
		images.setObjectUrl(url);
		images.setSize(size);
		images.setContentType(contentType);
	}	
	
	public void mergeData(Attachment images, String key, String url) {
		images.setObjectKey(key);
		images.setObjectUrl(url);
	}
}
