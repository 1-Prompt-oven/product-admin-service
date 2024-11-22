package org.example.productadminservice.llm.infrastructure;

import java.util.List;

import org.example.productadminservice.llm.domain.LLMVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LLMVersionRepository extends JpaRepository<LLMVersion, Long> {

	boolean existsByLlmVersionName(String llmVersionName);
}
