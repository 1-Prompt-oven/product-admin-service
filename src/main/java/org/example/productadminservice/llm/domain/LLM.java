package org.example.productadminservice.llm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class LLM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long llmId;

	@Column(nullable = false, length = 20)
	private String llmName;

	@Column(nullable = false)
	private boolean deleted;

	@Column(nullable = false)
	private String llmType;
}
