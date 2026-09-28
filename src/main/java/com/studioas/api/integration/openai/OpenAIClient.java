package com.studioas.api.integration.openai;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAIClient {
	private final RestClient restClient;
	private final String apiKey;
	private final String model;
	private final String systemPrompt;

	public OpenAIClient(
			@Value("${integrations.openai.api-key:}") String apiKey,
			@Value("${integrations.openai.model:gpt-4.1-mini}") String model,
			@Value("${integrations.openai.system-prompt}") String systemPrompt) {
		this.restClient = RestClient.builder()
				.baseUrl("https://api.openai.com/v1")
				.build();
		this.apiKey = apiKey;
		this.model = model;
		this.systemPrompt = systemPrompt;
	}

	public String generateReply(String userMessage) {
		if (apiKey.isBlank()) {
			throw new IllegalStateException("OpenAI API key is not configured");
		}

		Map<String, Object> request = Map.of(
				"model", model,
				"messages", List.of(
						Map.of("role", "system", "content", systemPrompt),
						Map.of("role", "user", "content", userMessage)));

		JsonNode response = restClient.post()
				.uri("/chat/completions")
				.header("Authorization", "Bearer " + apiKey)
				.body(request)
				.retrieve()
				.body(JsonNode.class);

		String reply = response == null
				? ""
				: response.path("choices").path(0).path("message").path("content").asText("").trim();
		if (reply.isBlank()) {
			throw new IllegalStateException("OpenAI returned an empty reply");
		}
		return reply.length() > 4000 ? reply.substring(0, 4000) : reply;
	}
}