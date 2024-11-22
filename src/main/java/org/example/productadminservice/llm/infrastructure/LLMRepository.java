package org.example.productadminservice.llm.infrastructure;

import java.util.List;
import java.util.Optional;

import org.example.productadminservice.llm.domain.LLM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LLMRepository extends JpaRepository<LLM, Long> {
	Optional<LLM> findByLlmName(String llmName);

}
