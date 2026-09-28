package com.studioas.api.service;

import com.studioas.api.integration.openai.OpenAIClient;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {
	private final OpenAIClient openAIClient;

	public WebhookService(OpenAIClient openAIClient) {
		this.openAIClient = openAIClient;
	}

	public String reply(String message) {
		return openAIClient.generateReply(message);
	}
}