package com.tom.service.knowledges.notes;

import java.util.HashSet;
import java.util.Set;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.image.Image;
import com.tom.service.knowledges.model.Auditable;
import com.tom.service.knowledges.tag.Tag;
import com.tom.service.knowledges.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "notes", indexes = {
		@Index(name = "idx_note_name", columnList = "note_name")
})
public class Note extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "note_name",
		length = 150,
		nullable = false, 
		unique = true)
	private String name;
	
	@Column(name = "description", 
		nullable = true, 
		unique = false)
	private String description;

	@Lob
	@Column(name = "annotation", 
			nullable = true, 
			unique = false)
	private byte[] annotation;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id")
	private Image image;

	@OneToMany(
			mappedBy = "notes",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY
	)
	private Set<Attachment> attachments = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { 
			CascadeType.PERSIST, 
			CascadeType.MERGE })
	@JoinTable(name = "notes_tags", 
			joinColumns = {@JoinColumn(name = "notes_id")},
			inverseJoinColumns = {@JoinColumn(name = "tags_id")})
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", nullable = false)
	private User user;
	
}
