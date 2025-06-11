package com.tom.service.knowledges.tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tag")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class TagController {

	private final TagService service;
	
	@GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagPageResponse> searchAllTags(@RequestParam(defaultValue = "0") int page) {
		var response = service.findAll(page);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/search/name", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagPageResponse> searchTagByName(@RequestParam String name, @RequestParam(defaultValue = "0") int page) {
		var response = service.findByName(name, page);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(value = "/new",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagResponse> createTag(@RequestBody CreateTagRequest request) {
		var response = service.createTag(request.name());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping(value = "/rename",
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagResponse> renameTag(@RequestBody RenameTagRequest request) {
		var response = service.renameTagByName(request.currentName(), request.newName());
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	
	@DeleteMapping(value = "/delete")
	public ResponseEntity<String> deleteTag(@RequestParam String name) {
		service.deleteTagByName(name);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted");
	}
	
}
