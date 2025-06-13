package com.globallogic.ejercicio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserExampleTest {

    @Test
    void shouldCoverLombokAndUserDetailsMethods() {
        UUID id = UUID.randomUUID();
        String name = "Sebastian";
        String email = "ignacio.pavez.p@gmail.com";
        String password = "a2asfGfdfdf4";
        LocalDateTime now = LocalDateTime.now();

        UserExample user = new UserExample();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreated(now);
        user.setLastLogin(now);
        user.setActive(true);

        // Probar getters
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(now, user.getCreated());
        assertEquals(now, user.getLastLogin());
        assertTrue(user.isActive());

        // Métodos de UserDetails
        assertEquals(email, user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void shouldInitializeFieldsOnCreate() {
        UserExample user = new UserExample();
        user.onCreate();

        assertNotNull(user.getCreated());
        assertNotNull(user.getLastLogin());
        assertTrue(user.isActive());
    }
}