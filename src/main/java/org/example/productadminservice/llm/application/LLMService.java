package org.example.productadminservice.llm.application;

import org.example.productadminservice.llm.dto.in.AddLLMRequestDto;
import org.example.productadminservice.llm.dto.in.DeleteLLMRequestDto;

public interface LLMService {

	void createLLM(AddLLMRequestDto addLLMRequestDto);

	void updateLLM(Long llmId, AddLLMRequestDto addLLMRequestDto);

	void deleteLLM(DeleteLLMRequestDto deleteLLMRequestDto);
}
