package com.studioas.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ManyChatReplyRequest(
        @NotBlank
        @Size(max = 4000)
        @JsonAlias({"last_text_input", "message"})
        String text) {
}