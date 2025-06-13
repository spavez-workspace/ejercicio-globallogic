package com.globallogic.ejercicio.service.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;

import com.globallogic.ejercicio.controller.UserController;
import com.globallogic.ejercicio.security.AuthEntryPointJwt;
import com.globallogic.ejercicio.security.JwtFilter;
import com.globallogic.ejercicio.security.WebSecurityConfig;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointJwtTest {

    private AuthEntryPointJwt entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new AuthEntryPointJwt();
    }

    @Test
    void commence_shouldSendUnauthorizedError() throws Exception {
        // Mockear objetos necesarios
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        // Ejecutar el método
        entryPoint.commence(request, response, authException);

        // Verificar que envió el error 401 con el mensaje correcto
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }
}