package com.globallogic.ejercicio.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.PhoneDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.exception.UserAlreadyExistsException;
import com.globallogic.ejercicio.exception.UserNotFoundException;
import com.globallogic.ejercicio.model.UserExample;
import com.globallogic.ejercicio.repository.UserRepository;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.model.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
    PasswordEncoder encoder;
	
	private UserExample dtoToEntity(SignUpRequestDto signUpDto) {
		
		UserExample user = new UserExample();
		
		user.setName(signUpDto.getName());
		user.setPassword(encoder.encode(signUpDto.getPassword()));
		user.setEmail(signUpDto.getEmail());
				
		List<Phone> phones = signUpDto.getPhones().stream()
				.map(p -> {
					Phone phone = new Phone();
					phone.setNumber(p.getNumber());
					phone.setCitycode(p.getCitycode());
					phone.setContrycode(p.getContrycode());
					phone.setUser(user);
					return phone;
				}).collect(Collectors.toList());
		
		user.setPhones(phones);
		return user;
		
	}
	
	@Override
	public SignUpResponseDto saveUser(SignUpRequestDto signUpDto) {		
		
		if(userRepository.existsByName(signUpDto.getName())) {
			throw new UserAlreadyExistsException("Usuario ya existe");
		}
		
		UserExample user = userRepository.save(dtoToEntity(signUpDto));		
		
		String token = jwtUtil.generateToken(user.getUsername());
		user.setActive(true);
		
		SignUpResponseDto response = userToSignUpResponse(user);
		response.setToken(token);
		
		return response;
	}
	
	/*
	 * Valida las credenciales, luego valida que el usuario coincida con el usuario del token
	 * si las condiciones se cumplen completa el login
	 * */
	@Override
	public LoginResponseDto login(LoginRequestDto request, String token) {		
		
		UserExample user = userRepository.findByName(request.getUser()).orElseThrow(() -> new UserNotFoundException("No existe usuario"));
		
		if (!encoder.matches(request.getPassword(), user.getPassword())) {
	        throw new BadCredentialsException("Contraseña incorrecta");
	    }
		
		jwtUtil.validateJwtToken(token);
		
		String userName = jwtUtil.getUserNameFromToken(token);
		
		if (!userName.equals(request.getUser())) {
			throw new BadCredentialsException("El token proporcionado no coincide con el usuario");
		}
				
		String newToken = jwtUtil.generateToken(userName);
		
		LoginResponseDto response = userToLoginResponse(user);
		response.setToken(newToken);
		response.setPassword(request.getPassword());
		
		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);
		
		return response;
	}
	
	private LoginResponseDto userToLoginResponse(UserExample user) {
		
		LoginResponseDto response = new LoginResponseDto();
		
		List<PhoneDto> phones = user.getPhones().stream()
				.map(p -> {
					PhoneDto phone = new PhoneDto();
					phone.setNumber(p.getNumber());
					phone.setCitycode(p.getCitycode());
					phone.setContrycode(p.getContrycode());
					return phone;
				}).collect(Collectors.toList());		
		
		response.setId(user.getId().toString());
		response.setCreated(user.getCreated().toString());
		response.setLastLogin(user.getLastLogin().toString());
		response.setActive(true);
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setPassword(user.getPassword());
		response.setPhones(phones);
		
		return response;
		
	}
	
	private SignUpResponseDto userToSignUpResponse(UserExample user) {
		
		SignUpResponseDto response = new SignUpResponseDto();
		response.setId(user.getId().toString());
		response.setCreated(user.getCreated().toString());
		response.setLastLogin(user.getLastLogin().toString());		
		response.setActive(user.isActive());
		
		return response;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByName(username)
	            .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con nombre: " + username));
	}
	
}
