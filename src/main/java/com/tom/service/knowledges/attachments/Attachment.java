package com.tom.service.knowledges.attachments;

import com.tom.service.knowledges.model.Auditable;
import com.tom.service.knowledges.notes.Note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "attachment", indexes = {
	    @Index(name = "idx_attachment_name_unique", 
	    		columnList = "attachment_name", 
	    		unique = true),
	    @Index(name = "idx_attachment_archivated", 
	    		columnList = "archivated"),
	    @Index(name = "idx_attachment_content_type", 
	    		columnList = "content_type"),
	    @Index(name = "idx_attachment_created_date", 
	    		columnList = "createdDate")
})
public class Attachment extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "attachment_name",
			nullable = false, 
			unique = true)
	private String name;
	
	@Column(name = "object_key",
			nullable = false, 
			unique = true)
	private String objectKey;

	@Column(name = "object_url", 
			nullable = false, 
			unique = true)
	private String objectUrl;

	@Column(name = "content_type", 
			nullable = false, 
			unique = false)
	private String contentType;
	
	@Column(name = "size", 
			nullable = false, 
			unique = false)
	private Long size;
	
	@Column(name = "archivated", 
			nullable = false, 
			unique = false)
	private Boolean archivated = false;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "note_id", nullable = false)
	private Note notes;
    
}
