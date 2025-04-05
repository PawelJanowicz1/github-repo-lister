package com.repofetcher.githubrepolister.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RepositoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testListRepositories_UserExists() throws Exception {
        String username = "paweljanowicz1";
        mockMvc.perform(get("/users/{username}/repos", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testListRepositories_UserNotFound() throws Exception {
        String username = "nonexistentuser";
        mockMvc.perform(get("/users/{username}/repos", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void testListRepositories_ResponseFormat() throws Exception {
        String username = "octocat";
        mockMvc.perform(get("/users/{username}/repos", username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].ownerLogin").exists())
                .andExpect(jsonPath("$[0].branches").isArray());
    }
}
