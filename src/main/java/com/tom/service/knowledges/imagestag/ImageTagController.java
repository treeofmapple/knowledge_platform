package com.tom.service.knowledges.imagestag;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/tag")
@RequiredArgsConstructor
public class ImageTagController {

	private final ImageTagService service;
	
	@GetMapping(value = "/get/all", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ImageTagResponse>> searchAllTags(){
		var response = service.searchAllTags();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/get/category", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ImageTagResponse>> searchCategory(@RequestParam String name) {
		var response = service.searchCategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@GetMapping(value = "/get/subcategory", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ImageTagResponse>> searchSubcategory(@RequestParam String name){
		var response = service.searchSubcategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping(value = "/new/category",
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemTagResponse> createCategory(@RequestParam String name){
		var response = service.createCategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value = "/new/subcategory",
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemTagResponse> createSubcategory(@RequestParam String name){
		var response = service.createSubcategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping(value = "/new/catsubcategory",
			consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemTagResponse> createCategorySubcategory(@RequestParam String category, @RequestParam String subcategory){
		var response = service.createCategorySubcategory(category, subcategory);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PutMapping(value = "/rename/category", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemTagResponse> editCategory(@RequestParam String category, @RequestParam("rename") String newName){
		var response = service.editCategory(category, newName);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping(value = "/rename/subcategory", 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ItemTagResponse> editSubcategory(@RequestParam("subcat") String category, @RequestParam("rename") String newName){
		var response = service.editCategory(category, newName);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping(value = "/delete/category")
	public ResponseEntity<ItemTagResponse> deleteCategory(@RequestParam String name){
		var response = service.deleteCategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@DeleteMapping(value = "/delete/subcategory")
	public ResponseEntity<ItemTagResponse> deleteSubcategory(@RequestParam String name){
		var response = service.deleteSubcategory(name);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
