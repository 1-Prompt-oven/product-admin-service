package org.example.productadminservice.llm.application;

import org.example.productadminservice.common.error.BaseException;
import org.example.productadminservice.common.response.BaseResponseStatus;
import org.example.productadminservice.llm.domain.LLMVersion;
import org.example.productadminservice.llm.dto.in.AddLLMVersionRequestDto;
import org.example.productadminservice.llm.infrastructure.LLMVersionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LLMVersionServiceImpl implements LLMVersionService {

	private final LLMVersionRepository llmVersionRepository;

	@Override
	public void addLLMVersion(AddLLMVersionRequestDto addLLMVersionRequestDto) {

		if (llmVersionRepository.existsByLlmVersionName(addLLMVersionRequestDto.getLlmVersionName())) {
			throw new BaseException(BaseResponseStatus.DUPLICATED_DATA);
		}

		llmVersionRepository.save(addLLMVersionRequestDto.createEntity());
	}

	@Override
	public void updateLLMVersion(Long llmVersionId, AddLLMVersionRequestDto addLLMVersionRequestDto) {

		llmVersionRepository.save(addLLMVersionRequestDto.updateEntity(llmVersionId));
	}

	@Override
	public void deleteLLMVersion(Long llmVersionId) {

		LLMVersion llmVersion = llmVersionRepository.findById(llmVersionId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

		llmVersionRepository.save(
			LLMVersion.builder()
				.llmVersionId(llmVersion.getLlmVersionId())
				.llmVersionName(llmVersion.getLlmVersionName())
				.llmId(llmVersion.getLlmId())
				.deleted(true)
				.build()
		);
	}

}
