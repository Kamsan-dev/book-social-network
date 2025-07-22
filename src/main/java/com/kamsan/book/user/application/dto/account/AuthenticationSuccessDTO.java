package com.kamsan.book.user.application.dto.account;

import com.kamsan.book.user.application.dto.ReadUserDTO;

public record AuthenticationSuccessDTO(String accessToken, String refreshToken, ReadUserDTO readUser) {}