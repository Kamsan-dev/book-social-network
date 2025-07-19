package com.kamsan.book.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamsan.book.user.domain.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByToken(String token);
}
