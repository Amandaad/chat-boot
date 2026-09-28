package com.studioas.api.controller;

import com.studioas.api.dto.ManyChatReplyRequest;
import com.studioas.api.dto.ManyChatReplyResponse;
import com.studioas.api.service.WebhookService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/webhooks/manychat")
public class WebhookController {
	private static final String SECRET_HEADER = "X-ManyChat-Secret";

	private final WebhookService webhookService;
	private final String expectedSecret;

	public WebhookController(
			WebhookService webhookService,
			@Value("${integrations.manychat.webhook-secret:}") String expectedSecret) {
		this.webhookService = webhookService;
		this.expectedSecret = expectedSecret;
	}

	@PostMapping("/reply")
	public ResponseEntity<?> createReply(
			@RequestHeader(name = SECRET_HEADER, required = false) String suppliedSecret,
			@Valid @RequestBody ManyChatReplyRequest request) {
		if (expectedSecret.isBlank()) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body("ManyChat webhook secret is not configured");
		}

		byte[] expectedBytes = expectedSecret.getBytes(StandardCharsets.UTF_8);
		byte[] suppliedBytes = suppliedSecret == null
				? new byte[0]
				: suppliedSecret.getBytes(StandardCharsets.UTF_8);
		if (!MessageDigest.isEqual(expectedBytes, suppliedBytes)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		try {
			return ResponseEntity.ok(new ManyChatReplyResponse(webhookService.reply(request.text())));
		} catch (IllegalStateException exception) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body("AI reply service is not configured");
		} catch (RestClientException exception) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body("AI reply service is temporarily unavailable");
		}
	}
}