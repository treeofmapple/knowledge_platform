package com.tom.service.knowledges.attachments;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AttachmentUtils {

	private final AttachmentRepository repository;
	
	public Attachment ensureObjectExistAndGet(String name) {
		return repository.findByName(name).orElseThrow(() -> {
            String message = String.format("Image with name %s not found", name);
            ServiceLogger.error(message);
            return new NotFoundException(message);
		});
	}
	
	public void checkIfObjectAlreadyExists(String name) {
		if (repository.existsByName(name)) {
			String message = "Object already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}

    public void checkIfAttachmentIsSame(Attachment currentName, String newName) {
    	repository.findByName(newName).ifPresent(existent -> {
			if(!existent.getId().equals(currentName.getId())) {
                throw new ConflictException("Attachment name '" + newName + "' is already in use.");
            }
    	});
    }
}
