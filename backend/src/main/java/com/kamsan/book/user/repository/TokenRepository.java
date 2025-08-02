package com.kamsan.book.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamsan.book.user.domain.Token;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByCode(String code);
	
	Optional<Token> findByVerificationToken(String token);
}
