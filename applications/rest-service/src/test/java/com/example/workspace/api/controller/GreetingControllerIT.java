package com.example.workspace.api.controller;

import com.example.workspace.test.base.AbstractDatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerIT extends AbstractDatabaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void create_returnsCreatedGreeting() throws Exception {
    mockMvc.perform(post("/api/greetings").param("name", "World"))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.message").value("Hello, World!"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void findById_returnsGreeting() throws Exception {
    String location = mockMvc.perform(post("/api/greetings").param("name", "Alice"))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getHeader("Location");

    mockMvc.perform(get(location))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Hello, Alice!"));
  }

  @Test
  void findById_returns404ForMissingId() throws Exception {
    mockMvc.perform(get("/api/greetings/999999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void findAll_returnsListOfGreetings() throws Exception {
    mockMvc.perform(post("/api/greetings").param("name", "Bob"));
    mockMvc.perform(post("/api/greetings").param("name", "Carol"));

    mockMvc.perform(get("/api/greetings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));
  }

  @Test
  void search_findsGreetingsByKeyword() throws Exception {
    mockMvc.perform(post("/api/greetings").param("name", "Dave"));

    mockMvc.perform(get("/api/greetings/search").param("keyword", "dave"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].message", containsString("Dave")));
  }

  @Test
  void search_returnsEmptyForNoMatch() throws Exception {
    mockMvc.perform(get("/api/greetings/search").param("keyword", "zzzznonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
  }
}

