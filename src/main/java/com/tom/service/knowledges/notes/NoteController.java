package com.tom.service.knowledges.notes;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/notes")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class NoteController {

	private final NoteService noteService;

	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findAllNotes(@RequestParam(defaultValue = "0") int value) {
		var response = noteService.findAllNotes(value);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@GetMapping(value = "/search/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findNoteByName(@RequestParam String name, @RequestParam(defaultValue = "0") int value) {
		var response = noteService.findNoteByName(name, value);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}

	@GetMapping(value = "/tag", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NotePageResponse> findNoteByTag(@RequestParam("name") String noteName, @RequestParam(defaultValue = "0") int value) {
		var response = noteService.findNoteByTag(noteName, value);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@PostMapping(value = "/create", 
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> createNote(@RequestBody @Valid CreateNoteRequest request) {
		var response = noteService.createNote(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value = "/edit/{noteName}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<NoteResponse> editNote(@PathVariable String noteName, @RequestBody @Valid EditNoteRequest request) {
		var response = noteService.editNote(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping(value = "/delete")
	public ResponseEntity<Void> deleteNote(@RequestParam String name) {
		noteService.deleteNote(name);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
