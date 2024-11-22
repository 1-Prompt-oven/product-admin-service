package org.example.productadminservice.llm.application;

import java.util.List;

import org.example.productadminservice.llm.infrastructure.LLMRepository;
import org.example.productadminservice.llm.infrastructure.LLMVersionRepository;
import org.example.productadminservice.llm.domain.LLM;
import org.example.productadminservice.llm.domain.LLMVersion;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(2)
public class LLMDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	private final LLMRepository llmRepository;
	private final LLMVersionRepository llmVersionRepository;

	@Override
	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {
		initializeLLMData();
	}

	private void initializeLLMData() {
		if (llmRepository.count() > 0) {
			log.info("LLM data already exists. Skip initializing LLM data.");
			return;
		}

		try {
			// LLM 데이터 초기화
			LLM dallE = LLM.builder()
				.llmName("Dall-E")
				.llmType("image")
				.deleted(false)
				.build();

			LLM gpt = LLM.builder()
				.llmName("GPT")
				.llmType("text")
				.deleted(false)
				.build();

			List<LLM> savedLlms = llmRepository.saveAll(List.of(dallE, gpt));

			// LLM 버전 데이터 초기화
			Long dallEId = savedLlms.get(0).getLlmId();
			Long gptId = savedLlms.get(1).getLlmId();

			List<LLMVersion> versions = List.of(
				// Dall-E 버전들
				LLMVersion.builder()
					.llmVersionName("dall-e-2")
					.llmId(dallEId)
					.deleted(false)
					.build(),
				LLMVersion.builder()
					.llmVersionName("dall-e-3")
					.llmId(dallEId)
					.deleted(false)
					.build(),

				// GPT 버전들
				LLMVersion.builder()
					.llmVersionName("gpt-3.5-turbo")
					.llmId(gptId)
					.deleted(false)
					.build(),
				LLMVersion.builder()
					.llmVersionName("gpt-4")
					.llmId(gptId)
					.deleted(false)
					.build(),
				LLMVersion.builder()
					.llmVersionName("gpt-4-turbo")
					.llmId(gptId)
					.deleted(false)
					.build()
			);

			llmVersionRepository.saveAll(versions);

			log.info("Successfully initialized {} LLMs with {} versions",
				savedLlms.size(), versions.size());

		} catch (Exception e) {
			log.error("Failed to initialize LLM data", e);
			throw e;
		}
	}
}