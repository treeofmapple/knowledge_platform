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
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"image", "attachments", "tags", "user"})
@EqualsAndHashCode(callSuper = true, exclude = {"image", "attachments", "tags", "user"})
@Table(name = "notes", indexes = {
		@Index(name = "idx_note_name", columnList = "note_name")
})
public class Note extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "note_name",
		length = 120,
		nullable = false, 
		unique = true)
	private String name;
	
	@Column(name = "description",
		length = 400,
		nullable = true, 
		unique = false)
	private String description;

	@Lob
	@Column(name = "annotation", 
			nullable = true, 
			unique = false)
	// put all the text onto a byte file to be store it easily
	private byte[] annotation;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_id", referencedColumnName = "id")
	// Must provide an image to be set to be show what project it is or no
	private Image image;

	@OneToMany(
			mappedBy = "note",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY
	)
	// can have attachments or no that is files images
	private Set<Attachment> attachments = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { 
			CascadeType.PERSIST, 
			CascadeType.MERGE })
	@JoinTable(name = "notes_tags", 
			joinColumns = {@JoinColumn(name = "notes_id")},
			inverseJoinColumns = {@JoinColumn(name = "tags_id")})
	// can be null no tag
	private Set<Tag> tags = new HashSet<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id", nullable = false)
	// it must be attached to an user
	private User user;
	
}
