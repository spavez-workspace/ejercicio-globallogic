package com.globallogic.ejercicio.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PhoneTests {

    @Test
    void shouldCoverLombokGeneratedMethods() {
        Phone phone = new Phone();
        phone.setId(1L);
        phone.setNumber(967890794L);
        phone.setCitycode(2);
        phone.setContrycode("56");

        UserExample user = new UserExample();
        user.setId(UUID.randomUUID());
        phone.setUser(user);

        assertEquals(1L, phone.getId());
        assertEquals(967890794L, phone.getNumber());
        assertEquals(2, phone.getCitycode());
        assertEquals("56", phone.getContrycode());
        assertEquals(user, phone.getUser());
    }
}