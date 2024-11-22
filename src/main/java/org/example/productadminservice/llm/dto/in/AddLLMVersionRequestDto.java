package org.example.productadminservice.llm.dto.in;

import org.example.productadminservice.llm.domain.LLMVersion;
import org.example.productadminservice.llm.vo.in.AddLLMVersionRequestVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddLLMVersionRequestDto {

	private String llmVersionName;
	private Long llmId;

	public static AddLLMVersionRequestDto toDto(AddLLMVersionRequestVo addLLMVersionRequestVo) {
		return AddLLMVersionRequestDto.builder()
				.llmId(addLLMVersionRequestVo.getLlmId())
				.llmVersionName(addLLMVersionRequestVo.getLlmVersionName())
				.build();
	}

	public LLMVersion createEntity() {
		return LLMVersion.builder()
				.llmVersionName(llmVersionName)
				.llmId(llmId)
				.deleted(false)
				.build();
	}

	public LLMVersion updateEntity(Long llmVersionId) {
		return LLMVersion.builder()
				.llmVersionId(llmVersionId)
				.llmId(llmId)
				.llmVersionName(llmVersionName)
				.deleted(false)
				.build();
	}
}
