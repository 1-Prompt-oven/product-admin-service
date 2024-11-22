package org.example.productadminservice.llm.vo.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLLMRequestVo {

	private Long llmId;
	private String name;
	private Long llmTypeId;
	private String llmTypeName;
}
