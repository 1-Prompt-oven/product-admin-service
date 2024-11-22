package org.example.productadminservice.llm.vo.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AddLLMVersionRequestVo {

	private String llmVersionName;
	private Long llmId;
}
