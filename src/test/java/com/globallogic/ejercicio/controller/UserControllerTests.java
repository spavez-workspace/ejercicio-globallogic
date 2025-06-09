package com.globallogic.ejercicio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.globallogic.ejercicio.security.JwtFilter;
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
    void login_shouldReturnOk() throws Exception {
    	String jsonRequest = "{"
    	        + "\"user\": \"Sebastian\","
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
        when(userService.login(any(LoginRequestDto.class), anyString()))
            .thenReturn(responseDto);         
        
        
        mockMvc.perform(post("/login")
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

}
