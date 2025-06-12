package com.globallogic.ejercicio.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDto {

	@NotBlank
	@Email(message = "Formato incorrecto para email")
	@Schema(description = "Email del usuario", example = "ignacio.pavez.p@gmail.com")
	private String email;
	@NotBlank
	@Schema(description = "Password del usuario", example = "a2asfGfdfdf4")
	private String password;
	
}
