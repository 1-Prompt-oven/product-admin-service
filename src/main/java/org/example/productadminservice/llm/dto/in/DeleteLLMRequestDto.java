package org.example.productadminservice.llm.dto.in;

import org.example.productadminservice.llm.domain.LLM;
import org.example.productadminservice.llm.vo.in.DeleteLLMRequestVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class DeleteLLMRequestDto {

	private Long llmId;

	public static DeleteLLMRequestDto toDto(DeleteLLMRequestVo deleteLLMRequestVo) {
		return DeleteLLMRequestDto.builder()
				.llmId(deleteLLMRequestVo.getLlmId())
				.build();
	}

	public LLM toEntity(LLM llm) {
		return LLM.builder()
			.llmId(llmId)
			.llmName(llm.getLlmName())
			.llmType(llm.getLlmType())
			.deleted(true)
			.build();
	}
}
