package com.tom.service.knowledges.image;

import com.tom.service.knowledges.attachments.Attachment;
import com.tom.service.knowledges.notes.Note;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "image")
@PrimaryKeyJoinColumn(name = "id")
public class Image extends Attachment {

	@OneToOne(mappedBy = "image")
	private Note note;
}
