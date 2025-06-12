package com.globallogic.ejercicio.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.model.UserExample;

public interface UserService {
	
	SignUpResponseDto saveUser(SignUpRequestDto signUpDto);
	LoginResponseDto loginByRequestBody(LoginRequestDto request, String token);
	LoginResponseDto login(String token);
	UserDetails loadUserByUsername(String username);

}
