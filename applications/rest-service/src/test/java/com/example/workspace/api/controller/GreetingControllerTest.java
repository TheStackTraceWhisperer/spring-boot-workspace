package com.example.workspace.api.controller;

import com.example.springlibrary.service.MyService;
import com.example.workspace.core.domain.Greeting;
import com.example.workspace.core.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private GreetingService greetingService;

  @MockitoBean
  private MyService myService;

  @Test
  void create_returnsCreatedWithLocationHeader() throws Exception {
    Greeting greeting = new Greeting("Hello, World!");
    // Use reflection to set the id since there's no setter
    var idField = Greeting.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(greeting, 1L);

    when(greetingService.create("World")).thenReturn(greeting);

    mockMvc.perform(post("/api/greetings").param("name", "World"))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/api/greetings/1"))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.message").value("Hello, World!"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty());
  }

  @Test
  void findById_returnsGreetingWhenFound() throws Exception {
    Greeting greeting = new Greeting("Hello, Alice!");
    var idField = Greeting.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(greeting, 42L);

    when(greetingService.findById(42L)).thenReturn(Optional.of(greeting));

    mockMvc.perform(get("/api/greetings/42"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(42))
        .andExpect(jsonPath("$.message").value("Hello, Alice!"));
  }

  @Test
  void findById_returns404WhenNotFound() throws Exception {
    when(greetingService.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/greetings/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void findAll_returnsList() throws Exception {
    Greeting g1 = new Greeting("Hello, Bob!");
    Greeting g2 = new Greeting("Hello, Carol!");

    when(greetingService.findAll()).thenReturn(List.of(g1, g2));

    mockMvc.perform(get("/api/greetings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].message").value("Hello, Bob!"))
        .andExpect(jsonPath("$[1].message").value("Hello, Carol!"));
  }

  @Test
  void search_returnsMatchingGreetings() throws Exception {
    Greeting greeting = new Greeting("Hello, Dave!");

    when(greetingService.search("dave")).thenReturn(List.of(greeting));

    mockMvc.perform(get("/api/greetings/search").param("keyword", "dave"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].message", containsString("Dave")));
  }

  @Test
  void search_returnsEmptyListWhenNoMatch() throws Exception {
    when(greetingService.search(anyString())).thenReturn(List.of());

    mockMvc.perform(get("/api/greetings/search").param("keyword", "nonexistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void greet_returnsMyServiceGreeting() throws Exception {
    when(myService.greet()).thenReturn("Hello from MyService!");

    mockMvc.perform(get("/api/greetings/greet"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from MyService!"));
  }
}

