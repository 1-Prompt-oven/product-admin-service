package org.example.productadminservice.llm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class LLMVersion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long llmVersionId;

	@Column(nullable = false)
	private String llmVersionName;

	@Column(nullable = false)
	private Long llmId;

	@Column(nullable = false)
	private boolean deleted;
}
