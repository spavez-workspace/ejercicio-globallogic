package com.globallogic.ejercicio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.PhoneDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.model.Phone;
import com.globallogic.ejercicio.model.UserExample;
import com.globallogic.ejercicio.repository.UserRepository;
import com.globallogic.ejercicio.security.util.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;
    
    @Mock
    private PasswordEncoder encoder;
    
    private UserExample user;
    
    @BeforeEach
    void setUp() {
        user = new UserExample();
        user.setId(UUID.randomUUID());
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setEmail("ignacio.pavez.p@gmail.com");
        user.setName("Sebastian");
        user.setPassword("encoded-password");

        Phone phone = new Phone();
        phone.setNumber(967890794L);
        phone.setCitycode(2);
        phone.setContrycode("56");
        phone.setUser(user);
        user.setPhones(List.of(phone));
    }
    
    @Test
    void saveUser_shouldReturnSignUpResponseDto() {
    	
    	SignUpRequestDto requestDto = new SignUpRequestDto();
    	requestDto.setName("Sebastian");
    	requestDto.setEmail("ignacio.pavez.p@gmail.com");
    	requestDto.setPassword("a2asfGfdfdf4");
    	PhoneDto phoneDto = new PhoneDto();
    	phoneDto.setNumber(967890794L);
    	phoneDto.setCitycode(2);
    	phoneDto.setContrycode("56");
    	requestDto.setPhones(List.of(phoneDto));
    	    	
    	when(jwtUtil.generateToken(any(String.class))).thenReturn("fake-jwt-token");
    	when(userRepository.save(any(UserExample.class))).thenReturn(user);
    	    	
    	SignUpResponseDto responseDto = userService.saveUser(requestDto);
    	
    	assertNotNull(responseDto.getId(), "El ID no debe ser null");
    	assertNotNull(responseDto.getCreated(), "La fecha de creación no debe ser null");
    	assertNotNull(responseDto.getLastLogin(), "La fecha de último login no debe ser null");
    	assertEquals("fake-jwt-token", responseDto.getToken(), "El token no coincide");
    	assertTrue(responseDto.isActive(), "El usuario debe estar activo");
    	
    }
    
    @Test
    void login_shouldReturnLoginResponseDto() {
    	
    	LoginRequestDto requestDto = new LoginRequestDto();
    	requestDto.setUser("Sebastian");
    	requestDto.setPassword("a2asfGfdfdf4");
    	
    	String token = "fake-jwt-token";
    	    	
    	when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user));
    	when(encoder.matches("a2asfGfdfdf4", "encoded-password")).thenReturn(true);
    	when(jwtUtil.validateJwtToken("fake-jwt-token")).thenReturn(true);
    	when(jwtUtil.getUserNameFromToken("fake-jwt-token")).thenReturn("Sebastian");
    	when(jwtUtil.generateToken("Sebastian")).thenReturn("new-generated-token");
    	when(userRepository.save(any(UserExample.class))).thenAnswer(invocation -> invocation.getArgument(0));
    	
    	LoginResponseDto responseDto = userService.login(requestDto, token);
    	
    	assertNotNull(responseDto.getId(), "El ID no debe ser null");
    	assertNotNull(responseDto.getCreated(), "La fecha de creación no debe ser null");
    	assertNotNull(responseDto.getLastLogin(), "La fecha de último login no debe ser null");
    	assertEquals("new-generated-token", responseDto.getToken(), "El token no coincide");
    	assertTrue(responseDto.isActive(), "El usuario debe estar activo");
    	assertEquals("Sebastian", responseDto.getName(), "El nombre no coincide");
    	assertEquals("ignacio.pavez.p@gmail.com", responseDto.getEmail(), "El email no coincide");
    	assertEquals("a2asfGfdfdf4", responseDto.getPassword(), "La password no coincide");
    	assertNotNull(user.getPhones(), "Usuario debe tener numero telefonico");
    	
    }
    
    @Test    
    void loadUserByName_shouldReturnUserDetailsEntity() {
    	
    	when(userRepository.findByName(any(String.class))).thenReturn(Optional.of(user));
    	
    	UserDetails userDetails = userService.loadUserByUsername("Sebastian");    	
    	
    	assertEquals("Sebastian", userDetails.getUsername(), "El nombre no coincide");
    	assertEquals("encoded-password", userDetails.getPassword(), "La password no coincide");
    }
    
}
