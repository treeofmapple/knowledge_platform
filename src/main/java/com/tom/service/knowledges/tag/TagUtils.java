package com.tom.service.knowledges.tag;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TagUtils {

	private final TagRepository repository;
	
    public Tag ensureTagExistsAndGet(String name) {
        return repository.findByNameIgnoreCase(name).orElseThrow(() -> {
            String message = "Tag with name: '" + name + "' not found.";
            ServiceLogger.warn(message);
            return new NotFoundException(message);
        });
    }
	
	public void checkIfTagAlreadyExists(String name) {
		if(repository.existsByNameIgnoreCase(name)) {
			String message = "Tag already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
	public void checkIfTagIsSame(Tag currentName, String newName) {
		repository.findByNameIgnoreCase(newName).ifPresent(existent -> {
			if(!existent.getId().equals(currentName.getId())) {
                throw new ConflictException("Tag name '" + newName + "' is already in use.");
            }
		});
		
	}
	
	
}
