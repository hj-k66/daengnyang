package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at is NULL")
public class RecordFile extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "record_id")   //일기 번호
	private Record record;

	private String storedFileUrl;
	private String uploadFilename;

	public static RecordFile makeRecordFile(String uploadFilename, String storedFileUrl,
			Record record) {
		return RecordFile.builder()
				.uploadFilename(uploadFilename)
				.storedFileUrl(storedFileUrl)
				.record(record)
				.build();
	}
}
