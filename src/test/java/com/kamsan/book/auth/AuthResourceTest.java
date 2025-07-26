package com.kamsan.book.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamsan.book.email.EmailService;
import com.kamsan.book.user.application.dto.account.RegisterUserDTO;
import com.kamsan.book.user.domain.User;
import com.kamsan.book.user.mapper.UserMapper;
import com.kamsan.book.user.repository.UserRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("Authentication API Integration Tests")
class AuthResourceTest {
	
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected UserRepository userRepository;

	@MockitoBean
	private JavaMailSender javaMailSender;

	@MockitoBean
	private EmailService emailService;
	
	@Autowired
	protected UserMapper userMapper;

	@Autowired
	protected EntityManager em;
    
    @Test
    @DisplayName("Register user successfully with valid data")
    void shouldRegisterUserSuccessfully() throws JsonProcessingException, Exception {
    	RegisterUserDTO dto = RegisterUserDTO
    						.builder()
    						.firstName("John")
    						.lastName("Doe")
    						.email("john.doe@email.com")
    						.password("password11").build(); 
	    
    	mockMvc.perform(post("/auth/register")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(dto)))
    	.andExpect(MockMvcResultMatchers.status().isAccepted());
    	
	    Optional<User> savedUser = userRepository.findByEmail("john.doe@email.com");
	    assertTrue(savedUser.isPresent());
	    assertEquals("John", savedUser.get().getFirstName());
	    
	    assertEquals(1, countTokensForUserEmail(savedUser.get().getEmail()));
	 
    }
    
    @Test
    @DisplayName("Register user failed with invalid data")
    void shouldNotRegisterUser() throws JsonProcessingException, Exception {
    	RegisterUserDTO dto = RegisterUserDTO
    						.builder()
    						.firstName("John")
    						.lastName("Doe")
    						.email("john.doeemail.com")
    						.password("passw").build(); 
	    
    	mockMvc.perform(post("/auth/register")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(dto)))
    	.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    
    public long countTokensForUserEmail(String email) {
        return ((Number) em.createNativeQuery("""
            SELECT COUNT(*) FROM token t
            JOIN bsn_user u ON t.user_id = u.user_id
            WHERE u.email = :email
        """)
        .setParameter("email", email)
        .getSingleResult()).longValue();
    }
}
