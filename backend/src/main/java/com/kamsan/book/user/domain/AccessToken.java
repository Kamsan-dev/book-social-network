package com.kamsan.book.user.domain;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;
import com.kamsan.book.user.enums.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "access_token")
@Builder
public class AccessToken extends AbstractAuditingEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "token", nullable = false, columnDefinition = "TEXT")
	private String token;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "token_type")
	private TokenType tokenType;
	
	@Column(name = "is_expired")
	private boolean expired;
	
	@Column(name = "is_revoked")
	private boolean revoked;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    AccessToken that = (AccessToken) o;
	    return id != null && id.equals(that.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}

}
