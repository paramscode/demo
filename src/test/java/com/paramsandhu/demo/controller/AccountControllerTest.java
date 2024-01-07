package com.paramsandhu.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import java.util.regex.Pattern;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAccountNumber() throws Exception {
        mockMvc.perform(get("/api/account/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesUuidPattern()));
    }

    private static org.hamcrest.Matcher<String> matchesUuidPattern() {
        return new org.hamcrest.TypeSafeMatcher<>() {
            @Override
            protected boolean matchesSafely(String item) {
                return Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
                        .matcher(item).matches();
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("a string that matches UUID pattern");
            }
        };
    }
}
