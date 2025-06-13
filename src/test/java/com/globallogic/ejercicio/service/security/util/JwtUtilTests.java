package com.globallogic.ejercicio.service.security.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import com.globallogic.ejercicio.exception.CustomJwtException;
import com.globallogic.ejercicio.security.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        jwtUtil = new JwtUtil();

        jwtUtil.jwtSecret = "estaEsUnaClaveMuyLargaParaProbarJwtUtil12345";
        jwtUtil.expirationSec = 300; // 1 hora

        // llamar init() para que se cree la key
        jwtUtil.init();
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String email = "ignacio.pavez.p@gmail.com";
        String token = jwtUtil.generateToken(email);

        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtUtil.key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(email, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void getEmailFromToken_shouldReturnEmail() {
        String email = "ignacio.pavez.p@gmail.com";
        String token = jwtUtil.generateToken(email);

        String extractedEmail = jwtUtil.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void resolveToken_shouldExtractTokenFromHeader() {
        var request = new MockHttpServletRequest();
        String token = "token123";

        request.addHeader("Authorization", "Bearer " + token);

        String result = jwtUtil.resolveToken(request);

        assertEquals(token, result);
    }

    @Test
    void validateJwtToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("ignacio.pavez.p@gmail.com");

        assertTrue(jwtUtil.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldThrowForMalformedToken() {
        String badToken = "malformed.token.here";

        var ex = assertThrows(CustomJwtException.class, () -> {
            jwtUtil.validateJwtToken(badToken);
        });

        assertTrue(ex.getMessage().contains("mal formado"));
    }
    
    @Test
    void validateJwtToken_shouldThrowExpiredJwtException() {
        // Crear token expirado (expiración en el pasado)
        String expiredToken = Jwts.builder()
                .setSubject("user@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10_000)) // 10s ago
                .setExpiration(new Date(System.currentTimeMillis() - 5_000))  // expirado hace 5s
                .signWith(jwtUtil.key, SignatureAlgorithm.HS256)
                .compact();

        CustomJwtException ex = assertThrows(CustomJwtException.class, () -> {
            jwtUtil.validateJwtToken(expiredToken);
        });

        assertEquals("Token JWT expirado", ex.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    void validateJwtToken_shouldThrowMalformedJwtException() {
        String malformedToken = "abc.def.ghi"; // Token inválido, mal formado

        CustomJwtException ex = assertThrows(CustomJwtException.class, () -> {
            jwtUtil.validateJwtToken(malformedToken);
        });

        assertTrue(ex.getMessage().contains("mal formado"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateJwtToken_shouldThrowIllegalArgumentException() {
        // Token vacío
        CustomJwtException ex = assertThrows(CustomJwtException.class, () -> {
            jwtUtil.validateJwtToken("");
        });

        assertTrue(ex.getMessage().contains("vacío"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void validateJwtToken_shouldThrowJwtException() {
        // Token inválido pero no entra en las otras categorías (ejemplo: firma incorrecta)
        String tokenWithBadSignature = Jwts.builder()
                .setSubject("user@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(Keys.hmacShaKeyFor("otraClaveSuperSecretaDePrueba1234567890".getBytes()), SignatureAlgorithm.HS256)
                .compact();

        CustomJwtException ex = assertThrows(CustomJwtException.class, () -> {
            jwtUtil.validateJwtToken(tokenWithBadSignature);
        });

        assertTrue(ex.getMessage().contains("inválido") || ex.getMessage().contains("manipulado"));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}