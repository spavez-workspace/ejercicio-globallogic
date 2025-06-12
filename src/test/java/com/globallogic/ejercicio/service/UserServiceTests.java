package com.globallogic.ejercicio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.PhoneDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.exception.CustomJwtException;
import com.globallogic.ejercicio.exception.UserAlreadyExistsException;
import com.globallogic.ejercicio.exception.UserNotFoundException;
import com.globallogic.ejercicio.model.Phone;
import com.globallogic.ejercicio.model.UserExample;
import com.globallogic.ejercicio.repository.UserRepository;
import com.globallogic.ejercicio.security.util.AESUtil;
import com.globallogic.ejercicio.security.util.JwtUtil;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

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
    private SignUpRequestDto requestSignUpDto;
    private LoginRequestDto requestLoginDto;
    
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
        
        requestSignUpDto = new SignUpRequestDto();
        requestSignUpDto.setName("Sebastian");
        requestSignUpDto.setEmail("ignacio.pavez.p@gmail.com");
        requestSignUpDto.setPassword("a2asfGfdfdf4");
    	PhoneDto phoneDto = new PhoneDto();
    	phoneDto.setNumber(967890794L);
    	phoneDto.setCitycode(2);
    	phoneDto.setContrycode("56");
    	requestSignUpDto.setPhones(List.of(phoneDto));
    	
    	requestLoginDto = new LoginRequestDto();
    	requestLoginDto.setEmail("ignacio.pavez.p@gmail.com");
    	requestLoginDto.setPassword("a2asfGfdfdf4");
    }
    
    @Test
    void saveUser_shouldReturnSignUpResponseDto() {
    	    	    	
    	try (MockedStatic<AESUtil> mockedStatic = mockStatic(AESUtil.class)) {
    		
    		mockedStatic.when(() -> AESUtil.encrypt(any(String.class), any(String.class)))
            .thenReturn("encryptedMockValue");
    		    	
    		when(userRepository.existsByEmail(requestSignUpDto.getEmail())).thenReturn(false);
	    	when(jwtUtil.generateToken(any(String.class))).thenReturn("fake-jwt-token");
	    	when(userRepository.save(any(UserExample.class))).thenReturn(user);
	    	    	
	    	SignUpResponseDto responseDto = userService.saveUser(requestSignUpDto);
	    	
	    	assertNotNull(responseDto.getId(), "El ID no debe ser null");
	    	assertNotNull(responseDto.getCreated(), "La fecha de creación no debe ser null");
	    	assertNotNull(responseDto.getLastLogin(), "La fecha de último login no debe ser null");
	    	assertEquals("fake-jwt-token", responseDto.getToken(), "El token no coincide");
	    	assertTrue(responseDto.isActive(), "El usuario debe estar activo");
    	
    	}
    	
    }
    
    @Test
    void loginByRequestBody_shouldReturnLoginResponseDto() {
    	    	
    	String token = "fake-jwt-token";
    	    
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
    	when(jwtUtil.validateJwtToken("fake-jwt-token")).thenReturn(true);
    	when(jwtUtil.getEmailFromToken("fake-jwt-token")).thenReturn("ignacio.pavez.p@gmail.com");
    	when(jwtUtil.generateToken("ignacio.pavez.p@gmail.com")).thenReturn("new-generated-token");
    	when(userRepository.save(any(UserExample.class))).thenAnswer(invocation -> invocation.getArgument(0));
    	
		try (MockedStatic<AESUtil> mockedStatic = mockStatic(AESUtil.class)) {
		
			ReflectionTestUtils.setField(userService, "aesSecretKey", "fake-secret");
    		mockedStatic.when(() -> AESUtil.decrypt(any(String.class), any(String.class)))
            .thenReturn("a2asfGfdfdf4");
		
    	
	    	LoginResponseDto responseDto = userService.loginByRequestBody(requestLoginDto, token);
	    	
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
    	
    }
    
    @Test
    void loginByRequestBody_shouldReturnBadCredentialsException_wrongToken() {
    	
    	String token = "fake-jwt-token";
    	
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
    	when(jwtUtil.getEmailFromToken(token)).thenReturn("otro.usuario@ejemplo.com");
    	
    	BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            userService.loginByRequestBody(requestLoginDto, token);
        });
    	
    	assertEquals("El token proporcionado no coincide con el usuario", exception.getMessage());
    	
    }
    
    @Test
	void loginByRequestBody_shouldReturnBadCredentialsException_wrongPassword() {
	    	
    	String token = "fake-jwt-token";
    	
    	ReflectionTestUtils.setField(userService, "aesSecretKey", "fake-secret");
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
    	when(jwtUtil.getEmailFromToken(token)).thenReturn(requestLoginDto.getEmail());
    	
    	try (MockedStatic<AESUtil> mockedStatic = mockStatic(AESUtil.class)) {
            mockedStatic.when(() -> AESUtil.decrypt(any(String.class), any(String.class)))
                .thenReturn("otraPassword");

            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                userService.loginByRequestBody(requestLoginDto, token);
            });

            assertEquals("Contraseña incorrecta", exception.getMessage());
        }
	    	
	}
    
    @Test
    void loginByRequestBody_shouldReturnUserNotFoundException() {
    	    		    	
    	String token = "fake-jwt-token";
    	
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
    	
    	UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.loginByRequestBody(requestLoginDto, token);
        });

        assertEquals("El correo ingresado no esta registrado", exception.getMessage());
	
    }
    
    @Test
    void login_shouldReturnLoginResponseDto() {
    	    	
    	String token = "fake-jwt-token";
    	    
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
    	when(jwtUtil.validateJwtToken("fake-jwt-token")).thenReturn(true);
    	when(jwtUtil.getEmailFromToken("fake-jwt-token")).thenReturn("ignacio.pavez.p@gmail.com");
    	when(jwtUtil.generateToken("ignacio.pavez.p@gmail.com")).thenReturn("new-generated-token");
    	when(userRepository.save(any(UserExample.class))).thenAnswer(invocation -> invocation.getArgument(0));
    	
		try (MockedStatic<AESUtil> mockedStatic = mockStatic(AESUtil.class)) {
		
			ReflectionTestUtils.setField(userService, "aesSecretKey", "fake-secret");
    		mockedStatic.when(() -> AESUtil.decrypt(any(String.class), any(String.class)))
            .thenReturn("a2asfGfdfdf4");
		
    	
	    	LoginResponseDto responseDto = userService.login(token);
	    	
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
    	
    }
    
    @Test
    void login_shouldReturnUserNotFoundException() {
    	    		    	
    	String token = "fake-jwt-token";
    	
    	when(jwtUtil.validateJwtToken(token)).thenReturn(true);
    	when(jwtUtil.getEmailFromToken(token)).thenReturn(requestLoginDto.getEmail());
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
    	
    	UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.login(token);
        });

        assertEquals("No existe el usuario correspondiente al token", exception.getMessage());
	
    }
    
    @Test    
    void loadUserByName_shouldReturnUserDetailsEntity() {
    	
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
    	
    	UserDetails userDetails = userService.loadUserByUsername("ignacio.pavez.p@gmail.com");    	
    	
    	assertEquals("ignacio.pavez.p@gmail.com", userDetails.getUsername(), "El email no coincide");
    	assertEquals("encoded-password", userDetails.getPassword(), "La password no coincide");
    }
    
    @Test    
    void loadUserByName_shouldReturnUsernameNotFoundException() {
    	
    	when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
    	
    	assertThrows(UsernameNotFoundException.class, () -> {
    		userService.loadUserByUsername("ignacio.pavez.p@gmail.com");
        });
    }
    
    @Test
    void saveUser_shouldReturnUserAlreadyExistsException() {
    	    		    	
		when(userRepository.existsByEmail(requestSignUpDto.getEmail())).thenReturn(true);    	
    	
    	assertThrows(UserAlreadyExistsException.class, () -> {
            userService.saveUser(requestSignUpDto);
        });
	
    }    
	    
}
