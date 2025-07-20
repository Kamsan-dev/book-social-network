package com.kamsan.book.user.application.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("Email address %s does not exist", username)));
	}
	
//	@Transactional(readOnly = true)
//	public ReadUserDTO getAuthenticatedUserFromSecurityContext() {
//		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		Optional<User> userOpt = userRepository.findOneByEmail(user.getEmail());
//		return userRepository.findOneByEmail(user.getEmail()).map(userMapper::userToReadUserDTO)
//				.orElseThrow(() -> new ApiException("Authenticated user was not found"));
//	}

}
