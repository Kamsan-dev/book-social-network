package com.kamsan.book.user.domain;

import java.time.OffsetDateTime;

import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Token extends AbstractAuditingEntity<Long> {

	@Id
	@GeneratedValue
	private Long id;
	private String token;
	@Column(name = "expires_at", nullable = false)
	private OffsetDateTime expiresAt;
	@Column(name = "validated_at", nullable = false)
	private OffsetDateTime validatedAt;
	
	@ManyToOne	
	@JoinColumn(name = "user_id", nullable = false)
	private User user;


}
