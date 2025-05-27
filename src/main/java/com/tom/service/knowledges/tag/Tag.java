package com.tom.service.knowledges.tag;

import java.util.HashSet;
import java.util.Set;

import com.tom.service.knowledges.model.Auditable;
import com.tom.service.knowledges.notes.Note;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "notes")
// @Cacheable
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "tag", indexes = {
		@Index(name = "idx_tag_name", columnList = "tag_name")
})
public class Tag extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "tag_name", 
		length = 50,
		nullable = false, 
		unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "notes", fetch = FetchType.LAZY)
	private Set<Note> notes = new HashSet<>();
	
}
