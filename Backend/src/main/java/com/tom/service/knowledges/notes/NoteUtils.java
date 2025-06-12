package com.tom.service.knowledges.notes;

import org.springframework.stereotype.Component;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.exception.ConflictException;
import com.tom.service.knowledges.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoteUtils {

	private final NoteRepository repository;
	
	public Note ensureNoteExistsAndGet(String name) {
		return repository.findByName(name).orElseThrow(() -> {
			String message = "Note with name: " + name + " not found.";
			ServiceLogger.warn(message);
			return new NotFoundException(message);
		});
	}
	
	public void checkIfNoteAlreadyExists(String name) {
		if(repository.existsByName(name)) {
			String message = "Note already exists: " + name;
			ServiceLogger.warn(message);
			throw new ConflictException(message);
		}
	}
	
	public void checkIfNoteNameIsSame(Note currentName, String newName) {
		repository.findByName(newName).ifPresent(existent -> {
			if(!existent.getId().equals(currentName.getId())) {
                throw new ConflictException("Note name '" + newName + "' is already in use.");
            }
		});
	}
}
