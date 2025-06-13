package com.globallogic.ejercicio.service.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.globallogic.ejercicio.controller.UserController;
import com.globallogic.ejercicio.dto.ErrorResponseDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.exception.handler.GlobalExceptionHandler;
import com.globallogic.ejercicio.security.JwtFilter;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.service.UserService;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTests {

	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private UserService userService;
	    
    @MockBean
    private JwtFilter jwtFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void signUp_shouldReturnBadRequest() throws Exception {
    	String badJson = "{"
    		    + "\"name\": \"Sebastian\","
    		    + "\"email\": \"ignacio.pavez.pgmail.com\","
    		    + "\"password\": \"a2asfGfdfdf4\","
    		    + "\"phones\": ["
    		    + "  {"
    		    + "    \"number\": 967890794,"
    		    + "    \"citycode\": 2,"
    		    + "    \"contrycode\": \"56\""
    		    + "  }"
    		    + "]"
    		    + "}";
        
        mockMvc.perform(post("/api/users/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
        	.andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(400))
            .andExpect(jsonPath("$.error.detail").exists());
    }
    
    @Test
    void handleGeneric_shouldReturnInternalServerErrorResponse() {
        // Simula una excepción cualquiera
        Exception ex = new Exception("Error inesperado");

        ResponseEntity<ErrorResponseDto> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getError()).isNotNull();
        assertThat(body.getError().getDetail()).isEqualTo("Error inesperado");
        assertThat(body.getError().getCodigo()).isEqualTo(500);
        assertThat(body.getError().getTimestamp()).isNotEmpty();
    }
}
