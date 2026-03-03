package com.example.workspace.core.service;

import com.example.workspace.core.domain.Greeting;
import com.example.workspace.test.base.AbstractDatabaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GreetingServiceTest extends AbstractDatabaseIntegrationTest {


  @Autowired
  private GreetingService greetingService;

  @Test
  void create_persistsAndReturnsGreeting() {
    Greeting greeting = greetingService.create("World");

    assertNotNull(greeting.getId());
    assertEquals("Hello, World!", greeting.getMessage());
    assertNotNull(greeting.getCreatedAt());
  }

  @Test
  void findById_returnsPersistedGreeting() {
    Greeting created = greetingService.create("Alice");

    Optional<Greeting> found = greetingService.findById(created.getId());

    assertTrue(found.isPresent());
    assertEquals(created.getId(), found.get().getId());
    assertEquals("Hello, Alice!", found.get().getMessage());
  }

  @Test
  void findById_returnsEmptyForMissingId() {
    Optional<Greeting> found = greetingService.findById(999_999L);

    assertTrue(found.isEmpty());
  }

  @Test
  void findAll_returnsAllGreetings() {
    greetingService.create("Bob");
    greetingService.create("Carol");

    List<Greeting> all = greetingService.findAll();

    assertTrue(all.size() >= 2);
  }

  @Test
  void search_findsGreetingsByKeyword() {
    greetingService.create("Dave");

    List<Greeting> results = greetingService.search("dave");

    assertFalse(results.isEmpty());
    assertTrue(results.stream().anyMatch(g -> g.getMessage().contains("Dave")));
  }

  @Test
  void search_returnsEmptyForNoMatch() {
    List<Greeting> results = greetingService.search("zzzznonexistent");

    assertTrue(results.isEmpty());
  }
}
