package com.globallogic.ejercicio.service.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigTests {

	@Autowired
    private MockMvc mockMvc;

	@Test
    void whenAccessingProtectedRouteWithoutToken_shouldFail() throws Exception {
        mockMvc.perform(post("/api/protected"))
                .andExpect(status().isUnauthorized());
    }
}
