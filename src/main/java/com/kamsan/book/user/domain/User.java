package com.kamsan.book.user.domain;

import com.kamsan.book.sharedkernel.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.*;

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
	@Column(name = "profile_image_id", unique = true)
	private String profileImageId;
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

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(publicId, user.publicId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), id, email, publicId);
	}

	@Override
	public String toString() {
		return "User{" +
				"roles=" + roles +
				", enabled=" + enabled +
				", accountLocked=" + accountLocked +
				", publicId=" + publicId +
				", profile_image_id='" + profileImageId + '\'' +
				", email='" + email + '\'' +
				", lastName='" + lastName + '\'' +
				", firstName='" + firstName + '\'' +
				", id=" + id +
				'}';
	}
}
