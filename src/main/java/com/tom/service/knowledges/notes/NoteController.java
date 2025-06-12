package com.tom.service.knowledges.notes;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tom.service.knowledges.tag.AttachTagRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/notes")
@RequiredArgsConstructor
public class NoteController {

	private final NoteService service;

	@PreAuthorize("hasRole('USER')")
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findAllNotes(@RequestParam(defaultValue = "0") int value, Principal connectedUser) {
		var response = service.findAllNotes(value, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping(value = "/search/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findNoteByName(@RequestParam String name, @RequestParam(defaultValue = "0") int value, Principal connectedUser) {
		var response = service.findNoteByName(name, value, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/search/public", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findAllPublicNotes(@RequestParam(defaultValue = "0") int page) {
		var response = service.findAllPublicNotes(page);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

    @PreAuthorize("permitAll()")
	@GetMapping(value = "/search/public/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findPublicNotesByName(@RequestParam String name, @RequestParam(defaultValue = "0") int page) {
		var response = service.findPublicNotesByName(name, page);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

    @PreAuthorize("permitAll()")
	@GetMapping(value = "/search/public/tag", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findPublicNotesByTag(@RequestParam String tag, @RequestParam(defaultValue = "0") int page) {
		var response = service.findPublicNotesByTag(tag, page);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping(value = "/private")
	public ResponseEntity<NoteResponse> setNotePublicOrPrivate(@RequestParam String name, Principal connectedUser) {
		var response = service.setNotePublicOrPrivate(name, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping(value = "/attach/note", 			
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> attachTagToNote(@RequestBody @Valid AttachTagRequest request, Principal connectedUser) {
		var response = service.attachTagToNote(request, connectedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping(value = "/attach/remove", 			
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> removeTagfromNote(@RequestBody @Valid AttachTagRequest request, Principal connectedUser) {
		var response = service.removeTagFromNote(request, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PostMapping(value = "/create", 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> createNote(@RequestBody @Valid CreateNoteRequest request, Principal connectedUser) {
		var response = service.createNote(request, connectedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping(value = "/edit/{noteName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> editNote(@PathVariable String noteName, @RequestBody @Valid EditNoteRequest request, Principal connectedUser) {
		var response = service.editNote(noteName, request, connectedUser);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteNote(@RequestParam String name, Principal connectedUser) {
		service.deleteNote(name, connectedUser);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}

