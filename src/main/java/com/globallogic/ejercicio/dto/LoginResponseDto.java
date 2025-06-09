package com.globallogic.ejercicio.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponseDto {

	private String id;
	private String created;
	private String lastLogin;
	private String token;
	private boolean isActive;
	private String name;
	private String email;
	private String password;
	private List<PhoneDto> phones;
	
}
