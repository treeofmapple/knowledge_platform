package com.tom.service.knowledges.notes;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tom.service.knowledges.common.ServiceLogger;
import com.tom.service.knowledges.common.SystemUtils;
import com.tom.service.knowledges.tag.AttachTagRequest;
import com.tom.service.knowledges.tag.TagUtils;
import com.tom.service.knowledges.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

	@Value("${application.page.size:10}")
	private int PAGE_SIZE;

	private final NoteRepository repository;
	private final NoteMapper mapper;
	private final SystemUtils utils;
	private final NoteUtils noteUtils;
	private final TagUtils tagUtils;

	private final ConcurrentHashMap<String, Object> noteCreationLocks = new ConcurrentHashMap<>();
	
	@Transactional(readOnly = true)
	public NotePageResponse findAllNotes(int value, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		Pageable pageable = PageRequest.of(value, PAGE_SIZE);
		Page<Note> tagNote = repository.findAllAccessibleNotes(user.getId(), pageable);
		return mapper.toNotePageResponse(tagNote);
	}
	
	@Transactional(readOnly = true)
	public NotePageResponse findNoteByName(String name, int value, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
		
		Pageable pageable = PageRequest.of(value, PAGE_SIZE);
		Page<Note> tagNote = repository.findByNameContainingIgnoreCaseAndAccessible(name, user.getId(), pageable);
		return mapper.toNotePageResponse(tagNote);
	}

	@Transactional(readOnly = true)
	public NotePageResponse findAllPublicNotes(int page) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Note> publicNotes = repository.findByNotePrivatedIsTrue(pageable);
		return mapper.toNotePageResponse(publicNotes);
	}

	@Transactional(readOnly = true)
	public NotePageResponse findPublicNotesByName(String name, int page) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Note> publicNotes = repository.findByNameContainingIgnoreCaseAndNotePrivatedIsTrue(name, pageable);
		return mapper.toNotePageResponse(publicNotes);
	}

	@Transactional(readOnly = true)
	public NotePageResponse findPublicNotesByTag(String tagName, int page) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);
		Page<Note> publicNotes = repository.findByTags_NameContainingIgnoreCaseAndNotePrivatedIsTrue(tagName, pageable);
		return mapper.toNotePageResponse(publicNotes);
	}
	
	@Transactional
	public NoteResponse attachTagToNote(AttachTagRequest request, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		var note = noteUtils.ensureNoteExistsAndGet(request.noteName());
		if (!note.getUser().getId().equals(user.getId())) {
			throw new SecurityException("User does not have permission to modify this note.");
		}
		
		var tag = tagUtils.ensureTagExistsAndGet(request.tagName());

		note.getTags().add(tag);
		var savedNote = repository.save(note);
		return mapper.toResponse(savedNote);
	}

	@Transactional
	public NoteResponse removeTagFromNote(AttachTagRequest request, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		var note = noteUtils.ensureNoteExistsAndGet(request.noteName());
		if (!note.getUser().getId().equals(user.getId())) {
			throw new SecurityException("User does not have permission to modify this note.");
		}

		var tag = tagUtils.ensureTagExistsAndGet(request.tagName());
		note.getTags().remove(tag);

		var savedNote = repository.save(note);
		return mapper.toResponse(savedNote);
	}

	@Transactional
	public NoteResponse setNotePublicOrPrivate(String noteName, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
	
		var note = noteUtils.ensureNoteExistsAndGet(noteName);
		if (!note.getUser().getId().equals(user.getId())) {
			throw new SecurityException("User does not have permission to modify this note.");
		}
		
		note.setNotePrivated(!note.getNotePrivated());
		
		var savedNote = repository.save(note);
		return mapper.toResponse(savedNote);
	}
	
	@Transactional
	public NoteResponse createNote(CreateNoteRequest request, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {} is creating note '{}'", userIp, request.name());

		String normalizedNoteName = request.name().trim().toLowerCase();
		Object lock = noteCreationLocks.computeIfAbsent(normalizedNoteName, k -> new Object());
		synchronized(lock) {
			try {
				noteUtils.checkIfNoteAlreadyExists(request.name());
				
				var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
				
				var newNote = mapper.build(request);
				newNote.setUser(user);
				
				byte[] annotationBytes = request.annotation().getBytes(StandardCharsets.UTF_8);
				newNote.setAnnotation(annotationBytes);
                if (newNote.getImage() != null) {
                    newNote.getImage().setNote(newNote);
                }
				
				var savedNote = repository.save(newNote);

				ServiceLogger.info("Note '{}' created successfully by user {}", savedNote.getName(), user.getUsername());
				return mapper.toResponse(savedNote);
				
			} finally {
				noteCreationLocks.remove(normalizedNoteName);
			}
		}
		
	}

	@Transactional
	public NoteResponse editNote(String noteName, EditNoteRequest request, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);

		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		var noteToUpdate = noteUtils.ensureNoteExistsAndGet(noteName);
        if (!noteToUpdate.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to edit this note.");
        }

		mapper.updateNoteFromRequest(noteToUpdate, request);

		if (request.annotation() != null && !request.annotation().isBlank()) {
			byte[] newAnnotationBytes = request.annotation().getBytes(StandardCharsets.UTF_8);
			noteToUpdate.setAnnotation(newAnnotationBytes);
		}

		var savedNote = repository.save(noteToUpdate);

		ServiceLogger.info("Note '{}' updated successfully by user {}", savedNote.getName(), user.getUsername());
		return mapper.toResponse(savedNote);
	}

	@Transactional
	public void deleteNote(String name, Principal connectedUser) {
		String userIp = utils.getUserIp();
		ServiceLogger.info("IP {}", userIp);
		
		var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

		var toRemove = noteUtils.ensureNoteExistsAndGet(name);
        if (!toRemove.getUser().getId().equals(user.getId())) {
            throw new SecurityException("User does not have permission to delete this note.");
        }
		
		repository.delete(toRemove);
		ServiceLogger.info(userIp);
	}

}
