package com.kamsan.book.sharedkernel.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class AbstractAuditingEntity<T> implements Serializable {

	public abstract T getId();

	@CreatedDate
	@Column(updatable = false, name = "created_date", nullable = false)
	private OffsetDateTime createdDate = OffsetDateTime.now();

	@LastModifiedDate
	@Column(name = "last_modified_date", insertable = false)
	private OffsetDateTime lastModifiedDate = OffsetDateTime.now();
}