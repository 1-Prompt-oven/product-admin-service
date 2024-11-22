package org.example.productadminservice.llm.application;

import org.example.productadminservice.llm.dto.in.AddLLMVersionRequestDto;

public interface LLMVersionService {

	void addLLMVersion(AddLLMVersionRequestDto addLLMVersionRequestDto);

	void updateLLMVersion(Long llmVersionId, AddLLMVersionRequestDto addLLMVersionRequestDto);

	void deleteLLMVersion(Long llmVersionId);

}
