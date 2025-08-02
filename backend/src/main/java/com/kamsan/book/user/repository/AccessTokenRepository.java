package com.kamsan.book.user.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kamsan.book.user.domain.AccessToken;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

	@Query("SELECT a FROM AccessToken a WHERE (a.expired = false AND a.revoked = false) AND a.user.publicId = :userPublicId")
	Set<AccessToken> findAllValidAccessTokens(UUID userPublicId);

	Optional<AccessToken> findByToken(String token);
	
}
