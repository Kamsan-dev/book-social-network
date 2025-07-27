package com.kamsan.book.user.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.catalina.WebResourceRoot.ArchiveIndexStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;
import com.kamsan.book.user.enums.RoleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "role")
@Builder
public class Role extends AbstractAuditingEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long id;

	@Column(unique = true)
	@Enumerated(EnumType.STRING)
	private RoleType name;

	@ManyToMany(mappedBy = "roles")
	@JsonIgnore
	private Set<User> users = new HashSet<>();

}
