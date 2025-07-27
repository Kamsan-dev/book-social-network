package com.kamsan.book.user.domain;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;
import com.kamsan.book.user.enums.RoleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "bsn_user")
@Builder
public class User extends AbstractAuditingEntity<Long> implements UserDetails, Principal {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequenceGenerator")
	@SequenceGenerator(name = "userSequenceGenerator", sequenceName = "user_generator", allocationSize = 1)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "email", nullable = false, unique = true)
	private String email;
	@Column(name = "image_url")
	private String imageUrl = "https://cdn-icons-png.flaticon.com/512/149/149071.png";
	@UuidGenerator
	@Column(name = "public_id", nullable = false, unique = true)
	private UUID publicId;
	@Column(name = "password")
	private String password;
	@Column(name = "is_account_locked")
	private boolean accountLocked;
	@Column(name = "is_enabled")
	private boolean enabled;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_roles", 
			joinColumns = @JoinColumn(name = "user_id"), 
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();
	
	// User Access Tokens
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AccessToken> accessTokens = new HashSet<>();
	
	// Token account validation
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Token> tokens = new ArrayList<>();

	@Override
	public String getName() {
		return this.email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		List<SimpleGrantedAuthority> authorities = new ArrayList<>(this.roles.stream()
			    .flatMap(role -> role.getName().getPermissions().stream())
			    .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
			    .toList());
		
		authorities.addAll(this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName().toString())).toList());
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

}
