package com.tom.service.knowledges.image;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.notes.Note;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "image", indexes = {
	    @Index(name = "idx_image_name_unique",
	            columnList = "attachment_name", 
	            unique = true),
	    @Index(name = "idx_image_archivated",
	            columnList = "archivated"),
	    @Index(name = "idx_image_content_type",
	            columnList = "content_type"),
	    @Index(name = "idx_image_created_date",
	            columnList = "createdDate")
	})
public class Image extends Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(mappedBy = "image")
	private Note note;
}
