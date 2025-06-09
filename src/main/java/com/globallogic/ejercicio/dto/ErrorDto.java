package com.globallogic.ejercicio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {

	@Schema(description = "Fecha y hora del error", example = "2025-06-09T00:15:18.0428172")
	private String timestamp;
	
	@Schema(description = "Codigo HTTP de respuesta", example = "409")
	private int codigo;
	
	@Schema(description = "Mensaje del error", example = "Usuario ya existe")
	private String detail;
	
}
