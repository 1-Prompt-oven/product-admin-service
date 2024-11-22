package org.example.productadminservice.llm.application;

import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.example.productadminservice.llm.domain.LLM;
import org.example.productadminservice.llm.dto.in.AddLLMRequestDto;
import org.example.productadminservice.llm.dto.in.DeleteLLMRequestDto;
import org.example.productadminservice.llm.infrastructure.LLMRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {

	private final LLMRepository llmRepository;

	@Override
	public void createLLM(AddLLMRequestDto addLLMRequestDto) {

		if (llmRepository.findByLlmName(addLLMRequestDto.getLlmName()).isPresent()) {
			throw new BaseException(BaseResponseStatus.DUPLICATED_DATA);
		}

		llmRepository.save(addLLMRequestDto.createEntity());
	}

	@Override
	public void updateLLM(Long llmId, AddLLMRequestDto addLLMRequestDto) {

		llmRepository.save(addLLMRequestDto.updateEntity(llmId));
	}

	@Override
	public void deleteLLM(DeleteLLMRequestDto deleteLLMRequestDto) {

		LLM llm = llmRepository.findById(deleteLLMRequestDto.getLlmId())
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

		llmRepository.save(deleteLLMRequestDto.toEntity(llm));

	}
}
