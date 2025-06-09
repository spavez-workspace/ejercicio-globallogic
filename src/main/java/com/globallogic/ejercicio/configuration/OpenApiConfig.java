package com.globallogic.ejercicio.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
	    info = @Info(title = "API Ejercicio GlobalLogic", version = "1.0", description = "Api desarrollada como prueba técnica, permite crear y loguear usuarios "
	    		+ "con el fin de obtener autorizacion mediante JWT")
	)
	@SecurityScheme(
	    name = "bearerAuth",               
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT",
	    description = "JWT Token requerido en el header Authorization: Bearer <token>"
	)
public class OpenApiConfig {

}
