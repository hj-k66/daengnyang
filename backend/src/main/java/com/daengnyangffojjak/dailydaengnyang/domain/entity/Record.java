package com.daengnyangffojjak.dailydaengnyang.domain.entity;

import com.daengnyangffojjak.dailydaengnyang.domain.dto.record.RecordWorkRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Where(clause = "deleted_at is NULL")
public class Record extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")   //작성자
	private User user;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	private Pet pet;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	private Tag tag;
	@Column(nullable = false)
	private String title;
	private String body;
	@Column(nullable = false)
	private Boolean isPublic;

	public void modifyRecord(RecordWorkRequest recordWorkRequest, Tag tag) {
		this.tag = tag;
		this.body = recordWorkRequest.getBody();
		this.title = recordWorkRequest.getTitle();
		this.isPublic = recordWorkRequest.getIsPublic();
	}
}
