package com.globallogic.ejercicio.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.globallogic.ejercicio.security.util.AESUtil;
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
	
	@Value("${aes.secret}")
	private String aesSecretKey;
	
	private UserExample dtoToEntity(SignUpRequestDto signUpDto) {
		
		UserExample user = new UserExample();
		
		user.setName(signUpDto.getName());
		user.setPassword(AESUtil.encrypt(signUpDto.getPassword(), aesSecretKey));
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
		
		//Valida si ya existe un usuario registrado con el mismo correo
		if(userRepository.existsByEmail(signUpDto.getEmail())) {
			throw new UserAlreadyExistsException("Ya existe un usuario registrado con ese correo.");
		}		
		
		//Almacena el usuario
		UserExample user = userRepository.save(dtoToEntity(signUpDto));		
		
		//Genera token JWT (Token no debe quedar en BD)
		String token = jwtUtil.generateToken(user.getEmail());
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
	public LoginResponseDto loginByRequestBody(LoginRequestDto request, String token) {		
		
		//Se valida que exista el usuario utilizando el correo recibido en el body
		UserExample user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("El correo ingresado no esta registrado"));
		
		//Obtiene el correo desencriptando el token
		String email = jwtUtil.getEmailFromToken(token);
		
		//Valida que el correo ingresado por body coincida con el recibido en el token
		if (!email.equals(request.getEmail())) {
			throw new BadCredentialsException("El token proporcionado no coincide con el usuario");
		}
		
		//Identifica al usuario con email y password, se desencripta con AES
		if(!AESUtil.decrypt(user.getPassword(), aesSecretKey).equals(request.getPassword())) {
			throw new BadCredentialsException("Contraseña incorrecta");
	    }
		
		//Valida que el token sea real
		jwtUtil.validateJwtToken(token);
		
		//Genera un nuevo token utilizando el email como claim
		String newToken = jwtUtil.generateToken(email);
		
		LoginResponseDto response = userToLoginResponse(user);
		response.setToken(newToken);
		response.setPassword(request.getPassword());
		
		user.setLastLogin(LocalDateTime.now());
		userRepository.save(user);
		
		return response;
	}
	
	@Override
	public LoginResponseDto login(String token){

		//Valida que el token sea real
		jwtUtil.validateJwtToken(token);
		
		//Obtiene el correo desencriptando el token
		String email = jwtUtil.getEmailFromToken(token);
		
		//Se valida que exista el usuario utilizando el correo encriptado en el token
		UserExample user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No existe el usuario correspondiente al token"));
						
		//Genera un nuevo token utilizando el email como claim
		String newToken = jwtUtil.generateToken(email);
		
		LoginResponseDto response = userToLoginResponse(user);
		response.setToken(newToken);
		
		//Desencripta la pass con AES
		response.setPassword(AESUtil.decrypt(user.getPassword(), aesSecretKey));
		
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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("No existe usuario con email: " + email));
	}
	
}
