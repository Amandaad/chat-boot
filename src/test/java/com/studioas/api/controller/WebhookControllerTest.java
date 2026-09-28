package com.studioas.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.studioas.api.dto.ManyChatReplyRequest;
import com.studioas.api.dto.ManyChatReplyResponse;
import com.studioas.api.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class WebhookControllerTest {
    private final WebhookService webhookService = mock(WebhookService.class);

    @Test
    void rejectsRequestWithIncorrectSecret() {
        WebhookController controller = new WebhookController(webhookService, "expected-secret");

        ResponseEntity<?> response = controller.createReply(
                "wrong-secret", new ManyChatReplyRequest("Oi"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(webhookService);
    }

    @Test
    void returnsGeneratedReplyForValidSecret() {
        WebhookController controller = new WebhookController(webhookService, "expected-secret");
        when(webhookService.reply("Oi")).thenReturn("Olá! Como posso ajudar?");

        ResponseEntity<?> response = controller.createReply(
                "expected-secret", new ManyChatReplyRequest("Oi"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Olá! Como posso ajudar?", ((ManyChatReplyResponse) response.getBody()).reply());
    }

    @Test
    void reportsUnavailableWhenSecretIsNotConfigured() {
        WebhookController controller = new WebhookController(webhookService, " ");

        ResponseEntity<?> response = controller.createReply(
                "any-secret", new ManyChatReplyRequest("Oi"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        verifyNoInteractions(webhookService);
    }
}