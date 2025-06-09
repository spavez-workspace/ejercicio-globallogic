package com.globallogic.ejercicio.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDto {

	@NotBlank
	@Schema(description = "Nombre de usuario", example = "spavez")
	private String user;
	@NotBlank
	@Schema(description = "Password del usuario", example = "a2asfGfdfdf4")
	private String password;
	
}
