package com.kamsan.book.user.domain;

import java.time.OffsetDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokenSequenceGenerator")
	@SequenceGenerator(name = "tokenSequenceGenerator", sequenceName = "token_generator", allocationSize = 1)
	private Long id;
	private String code;
	@Column(name = "expires_at", nullable = false)
	private OffsetDateTime expiresAt;
	@Column(name = "validated_at", nullable = true)
	private OffsetDateTime validatedAt;
	@Column(name ="verification_token", unique= true)
	private String verificationToken;
	
	@ManyToOne	
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;


}
