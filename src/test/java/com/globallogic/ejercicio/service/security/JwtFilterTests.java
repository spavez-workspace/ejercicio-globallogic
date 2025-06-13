package com.globallogic.ejercicio.service.security;

import com.globallogic.ejercicio.security.JwtFilter;
import com.globallogic.ejercicio.security.util.JwtUtil;
import com.globallogic.ejercicio.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Test
    void shouldAuthenticateWhenValidToken() throws Exception {
        String jwt = "valid-token";
        String email = "ignacio.pavez.p@gmail.com";

        when(request.getRequestURI()).thenReturn("/api/users/private");
        when(jwtUtil.resolveToken(request)).thenReturn(jwt);
        when(jwtUtil.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtil.getEmailFromToken(jwt)).thenReturn(email);
        when(userService.loadUserByUsername(email)).thenReturn(userDetails);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).resolveToken(request);
        verify(jwtUtil).validateJwtToken(jwt);
        verify(jwtUtil).getEmailFromToken(jwt);
        verify(userService).loadUserByUsername(email);
        verify(filterChain).doFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
    }

    @Test
    void shouldReturn401WhenExceptionThrown() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/private");
        when(jwtUtil.resolveToken(request)).thenThrow(new RuntimeException("invalid"));

        doNothing().when(response).sendError(anyInt(), anyString());

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), contains("invalid"));
        verifyNoMoreInteractions(response);

        verify(filterChain, never()).doFilter(any(), any());
    }
    
    @Test
    void shouldSkipFilterForSignUpPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/sign-up");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response); 
        verifyNoMoreInteractions(response);
    }

    @Test
    void shouldSkipFilterForLoginPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/login");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(response);
    }

    @Test
    void shouldSkipFilterForLoginByRequestBodyPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/loginByRequestBody");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(response);
    }

    @Test
    void shouldSkipFilterForH2ConsolePath() throws Exception {
        when(request.getRequestURI()).thenReturn("/h2-console/somepath");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(response);
    }

    @Test
    void shouldSkipFilterForSwaggerUIPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/swagger-ui/somepath");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(response);
    }

    @Test
    void shouldSkipFilterForApiDocsPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/v3/api-docs/somepath");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(response);
    }
    
    @Test
    void shouldNotAuthenticateWhenJwtIsNull() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/private");
        when(jwtUtil.resolveToken(request)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenJwtIsInvalid() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/private");
        when(jwtUtil.resolveToken(request)).thenReturn("some-token");
        when(jwtUtil.validateJwtToken("some-token")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}