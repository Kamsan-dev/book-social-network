package com.kamsan.book.user.application.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kamsan.book.user.application.dto.ReadUserDTO;

public record AuthenticationSuccessDTO(
		@JsonProperty("access_token") String accessToken,
		@JsonProperty("refresh_token") String refreshToken, 
		@JsonProperty("user") ReadUserDTO readUser) {
}