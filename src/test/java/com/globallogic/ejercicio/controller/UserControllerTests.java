package com.globallogic.ejercicio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.globallogic.ejercicio.controller.UserController;
import com.globallogic.ejercicio.dto.LoginRequestDto;
import com.globallogic.ejercicio.dto.LoginResponseDto;
import com.globallogic.ejercicio.dto.PhoneDto;
import com.globallogic.ejercicio.dto.SignUpRequestDto;
import com.globallogic.ejercicio.dto.SignUpResponseDto;
import com.globallogic.ejercicio.exception.CustomJwtException;
import com.globallogic.ejercicio.exception.UserAlreadyExistsException;
import com.globallogic.ejercicio.exception.UserNotFoundException;
import com.globallogic.ejercicio.security.JwtFilter;
import com.globallogic.ejercicio.security.util.AESUtil;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.service.UserService;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTests {

	@Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtFilter jwtFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
        
    @Test
    void signUp_shouldReturnCreated() throws Exception {
    	String jsonRequest = "{"
    		    + "\"name\": \"Sebastian\","
    		    + "\"email\": \"ignacio.pavez.p@gmail.com\","
    		    + "\"password\": \"a2asfGfdfdf4\","
    		    + "\"phones\": ["
    		    + "  {"
    		    + "    \"number\": 967890794,"
    		    + "    \"citycode\": 2,"
    		    + "    \"contrycode\": \"56\""
    		    + "  }"
    		    + "]"
    		    + "}";

        SignUpResponseDto responseDto = new SignUpResponseDto();
        responseDto.setId("userid");
        responseDto.setCreated(LocalDateTime.now().toString());
        responseDto.setLastLogin(LocalDateTime.now().toString());
        responseDto.setToken("token123");
        responseDto.setActive(true);

        when(userService.saveUser(any(SignUpRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("userid"))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.lastLogin").exists())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.active").value(true));
    }
    
    @Test
    void signUp_shouldReturnError_whenUserAlreadyExists() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token"))
            .thenThrow(new UserAlreadyExistsException("Ya existe un usuario registrado con ese correo."));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(409))
            .andExpect(jsonPath("$.error.detail").value("Ya existe un usuario registrado con ese correo."));
    }
    
    @Test
    void loginByRequestBody_shouldReturnOk() throws Exception {
    	String jsonRequest = "{"
    	        + "\"email\": \"ignacio.pavez.p@gmail.com\","
    	        + "\"password\": \"a2asfGfdfdf4\""
    	        + "}";
    	
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setId("userid");
        responseDto.setCreated(LocalDateTime.now().toString());
        responseDto.setLastLogin(LocalDateTime.now().toString());
        responseDto.setToken("fake-jwt-token");
        responseDto.setName("Sebastian");
        responseDto.setEmail("ignacio.pavez.p@gmail.com");
        responseDto.setPassword("a2asfGfdfdf4");
        responseDto.setActive(true);

        PhoneDto phone = new PhoneDto();
        phone.setNumber(967890794L);
        phone.setCitycode(2);
        phone.setContrycode("56");
        responseDto.setPhones(List.of(phone));
    		
        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.loginByRequestBody(any(LoginRequestDto.class), anyString()))
            .thenReturn(responseDto);
        
        mockMvc.perform(post("/loginByRequestBody")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("userid"))
            .andExpect(jsonPath("$.token").value("fake-jwt-token"))
            .andExpect(jsonPath("$.name").value("Sebastian"))
            .andExpect(jsonPath("$.email").value("ignacio.pavez.p@gmail.com"))
            .andExpect(jsonPath("$.password").value("a2asfGfdfdf4"))
            .andExpect(jsonPath("$.phones[0].number").value(967890794))
            .andExpect(jsonPath("$.phones[0].citycode").value(2))
            .andExpect(jsonPath("$.phones[0].contrycode").value("56"))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.lastLogin").exists());   
    }
    
    @Test
    void login_shouldReturnOk() throws Exception {
    	
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setId("userid");
        responseDto.setCreated(LocalDateTime.now().toString());
        responseDto.setLastLogin(LocalDateTime.now().toString());
        responseDto.setToken("fake-jwt-token");
        responseDto.setName("Sebastian");
        responseDto.setEmail("ignacio.pavez.p@gmail.com");
        responseDto.setPassword("a2asfGfdfdf4");
        responseDto.setActive(true);

        PhoneDto phone = new PhoneDto();
        phone.setNumber(967890794L);
        phone.setCitycode(2);
        phone.setContrycode("56");
        responseDto.setPhones(List.of(phone));
    		
        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token")).thenReturn(responseDto);
        
        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("userid"))
            .andExpect(jsonPath("$.token").value("fake-jwt-token"))
            .andExpect(jsonPath("$.name").value("Sebastian"))
            .andExpect(jsonPath("$.email").value("ignacio.pavez.p@gmail.com"))
            .andExpect(jsonPath("$.password").value("a2asfGfdfdf4"))
            .andExpect(jsonPath("$.phones[0].number").value(967890794))
            .andExpect(jsonPath("$.phones[0].citycode").value(2))
            .andExpect(jsonPath("$.phones[0].contrycode").value("56"))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.lastLogin").exists());   
    }
    
    @Test
    void login_shouldReturnError_whenExpiredJwtException() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("invalid-token");
        when(userService.login("invalid-token"))
            .thenThrow(new CustomJwtException("Token JWT expirado", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(401))
            .andExpect(jsonPath("$.error.detail").value("Token JWT expirado"));
    }
    
    @Test
    void login_shouldReturnError_whenUserNotFound() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token"))
            .thenThrow(new UserNotFoundException("No existe el usuario correspondiente al token"));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(404))
            .andExpect(jsonPath("$.error.detail").value("No existe el usuario correspondiente al token"));
    }
    
    @Test
    void loginByRequestBody_shouldReturnError_whenUserNotFound() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token"))
            .thenThrow(new UserNotFoundException("El correo ingresado no esta registrado"));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(404))
            .andExpect(jsonPath("$.error.detail").value("El correo ingresado no esta registrado"));
    }
    
    @Test
    void loginByRequestBody_shouldReturnError_whenBadCredentials_WrongPassword() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token"))
            .thenThrow(new BadCredentialsException("Contraseña incorrecta"));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(401))
            .andExpect(jsonPath("$.error.detail").value("Contraseña incorrecta"));
    }
    
    @Test
    void loginByRequestBody_shouldReturnError_whenBadCredentials_WrongToken() throws Exception {

        when(jwtUtil.resolveToken(any(HttpServletRequest.class))).thenReturn("fake-jwt-token");
        when(userService.login("fake-jwt-token"))
            .thenThrow(new BadCredentialsException("El token proporcionado no coincide con el usuario"));

        mockMvc.perform(post("/login")
                .header("Authorization", "Bearer fake-jwt-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.timestamp").exists())
            .andExpect(jsonPath("$.error.codigo").value(401))
            .andExpect(jsonPath("$.error.detail").value("El token proporcionado no coincide con el usuario"));
    }

}
