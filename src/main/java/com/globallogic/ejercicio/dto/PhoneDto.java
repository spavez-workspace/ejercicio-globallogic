package com.globallogic.ejercicio.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PhoneDto {

	@NotNull(message = "Debe ingresar al menos un telefono")
	@Schema(description = "Numero de telefono", example = "967890794")
	Long number;
	
	@NotNull(message = "Debe ingresar el codigo de ciudad")
	@Schema(description = "Codigo de ciudad", example = "2")
	Integer citycode;
	
	@NotBlank(message = "Debe ingresar codigo de pais")
	@Schema(description = "Codigo de pais", example = "56")
	String contrycode;
	
	
}
