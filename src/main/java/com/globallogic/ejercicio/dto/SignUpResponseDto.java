package com.globallogic.ejercicio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponseDto {

	private String id;	
	private String created;
	private String lastLogin;
	private String token;
	private boolean isActive;
	
}
