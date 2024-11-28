package org.example.productadminservice.llm.presentation;

import org.example.productadminservice.common.response.BaseResponse;
import org.example.productadminservice.llm.application.LLMVersionService;
import org.example.productadminservice.llm.dto.in.AddLLMVersionRequestDto;
import org.example.productadminservice.llm.vo.in.AddLLMVersionRequestVo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/product-admin/llm/version")
@Tag(name = "LLM 버전 관리", description = "LLM 버전 관리 API")
public class LLMVersionController {

	private final LLMVersionService LLMVersionService;

	@Operation(summary = "LLM 버전 생성", description = "LLM 버전 생성")
	@PostMapping
	public BaseResponse<Void> addLLMVersion(
		@RequestBody AddLLMVersionRequestVo addLLMVersionRequestVo) {

		LLMVersionService.addLLMVersion(AddLLMVersionRequestDto.toDto(addLLMVersionRequestVo));
		return new BaseResponse<>();
	}

	@Operation(summary = "LLM 버전 수정", description = "LLM 버전 수정")
	@PutMapping("/{llmVersionId}")
	public BaseResponse<Void> updateLLMVersion(
		@PathVariable Long llmVersionId, @RequestBody AddLLMVersionRequestVo addLLMVersionRequestVo) {

		LLMVersionService.updateLLMVersion(llmVersionId, AddLLMVersionRequestDto.toDto(addLLMVersionRequestVo));
		return new BaseResponse<>();
	}

	@Operation(summary = "LLM 버전 삭제", description = "LLM 버전 삭제")
	@DeleteMapping("/{llmVersionId}")
	public BaseResponse<Void> deleteLLMVersion(@PathVariable Long llmVersionId) {

		LLMVersionService.deleteLLMVersion(llmVersionId);
		return new BaseResponse<>();
	}

}
