package com.globallogic.ejercicio.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.globallogic.ejercicio.dto.ErrorResponseDto;
import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.service.UserService;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Operation(
	        summary = "Endpoint de creación de un usuario",
	        description = "Crea usuario, generando JWT y devolviendo información relacionada a la creación"
	    )	
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignUpResponseDto.class))),
	        @ApiResponse(responseCode = "409", description = "Usuario ya existe", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
	    })
	@PostMapping(value = "/sign-up", consumes = "application/json", produces = "application/json")
	public ResponseEntity<SignUpResponseDto> signUp(@RequestBody @Valid SignUpRequestDto request) {
		
		SignUpResponseDto response = userService.saveUser(request);		
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@Operation(
	        summary = "Endpoint de login de usuario un email y password",
	        description = "Dado un email y password y algun token del usuario generado previamente loguea al usuario otorgandole un nuevo token, se debe pasar JWT como header de autorizacion",
	        security = @SecurityRequirement(name = "bearerAuth")
	    )	
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
	        @ApiResponse(responseCode = "401", description = "Contraseña incorrecta", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
	        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))	        
	    })
	@PostMapping(value = "/loginByRequestBody", consumes = "application/json", produces = "application/json")
	public ResponseEntity<LoginResponseDto> loginByRequestBody(@RequestBody @Valid LoginRequestDto login, HttpServletRequest request){
			
		String token = jwtUtil.resolveToken(request);
		
		LoginResponseDto response = userService.loginByRequestBody(login, token);
				
		return ResponseEntity.ok(response);
	}
	
	@Operation(
	        summary = "Endpoint de login de usuario por token",
	        description = "Dado un token del usuario generado previamente loguea al usuario otorgandole un nuevo token, se debe pasar JWT como header de autorizacion",
	        security = @SecurityRequirement(name = "bearerAuth")
	    )	
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Login correcto", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
	        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))	        
	    })
	@PostMapping(value = "/login")
	public ResponseEntity<LoginResponseDto> login(HttpServletRequest request){
			
		String token = jwtUtil.resolveToken(request);
		
		LoginResponseDto response = userService.login(token);
				
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/private")
    public ResponseEntity<String> privateEndpoint() {
        return ResponseEntity.ok("Autenticado");
    }

}
