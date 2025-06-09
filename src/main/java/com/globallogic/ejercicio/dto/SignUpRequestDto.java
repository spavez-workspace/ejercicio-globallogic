package com.globallogic.ejercicio.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignUpRequestDto {

	@NotBlank(message = "El nombre es obligatorio")
	@Schema(description = "Nombre de usuario", example = "spavez")
	String name;
	
	@Email(message = "Formato incorrecto para email")
	@NotBlank(message = "El email es obligatorio")
	@Schema(description = "Email", example = "ignacio.pavez.p@gmail.com")
	String email;
		
	@NotBlank(message = "La password es obligatoria")
	@Pattern(
	        regexp = "^(?=(?:[^A-Z]*[A-Z]){1}[^A-Z]*$)(?=(?:[^0-9]*[0-9]){2}[^0-9]*$)[a-zA-Z0-9]{8,12}$",
	        message = "La contraseña debe tener de 8 a 12 caracteres, 1 mayuscula, 2 numeros y el resto minusculas."
	    )
	@Schema(description = "Password con formato correcto", example = "a2asfGfdfdf4")
	String password;
	
	@NotEmpty(message = "Debe ingresar un teléfono")
	@Valid
	List<PhoneDto> phones;
	
}
