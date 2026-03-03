package com.example.workspace.api.controller;

import com.example.springlibrary.service.MyService;
import com.example.workspace.core.domain.Greeting;
import com.example.workspace.core.service.GreetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/greetings")
@RequiredArgsConstructor
@Tag(name = "Greetings", description = "Operations for creating and retrieving greetings")
public class GreetingController {

  private final GreetingService greetingService;
  private final MyService myService;


  @Operation(summary = "Create a greeting", description = "Creates a new greeting for the given name")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Greeting created",
          content = @Content(schema = @Schema(implementation = Greeting.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<Greeting> create(
      @Parameter(description = "Name to greet", required = true, example = "World")
      @RequestParam String name) {
    Greeting greeting = greetingService.create(name);
    return ResponseEntity
        .created(URI.create("/api/greetings/" + greeting.getId()))
        .body(greeting);
  }

  @Operation(summary = "Find a greeting by ID", description = "Returns a single greeting by its ID")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Greeting found",
          content = @Content(schema = @Schema(implementation = Greeting.class))),
      @ApiResponse(responseCode = "404", description = "Greeting not found", content = @Content)
  })
  @GetMapping("/{id}")
  public ResponseEntity<Greeting> findById(
      @Parameter(description = "ID of the greeting", required = true, example = "1")
      @PathVariable Long id) {
    return greetingService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @Operation(summary = "List all greetings", description = "Returns all greetings")
  @ApiResponse(responseCode = "200", description = "List of greetings",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Greeting.class))))
  @GetMapping
  public List<Greeting> findAll() {
    return greetingService.findAll();
  }

  @Operation(summary = "Search greetings", description = "Searches greetings by keyword (case-insensitive)")
  @ApiResponse(responseCode = "200", description = "Matching greetings",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = Greeting.class))))
  @GetMapping("/search")
  public List<Greeting> search(
      @Parameter(description = "Keyword to search for in greeting messages", required = true, example = "hello")
      @RequestParam String keyword) {
    return greetingService.search(keyword);
  }

  @Operation(summary = "Greet", description = "Returns a simple greeting from MyService")
  @ApiResponse(responseCode = "200", description = "Greeting message",
      content = @Content(schema = @Schema(implementation = String.class)))
  @GetMapping("/greet")
  public String greet() {
    return myService.greet();
  }
}

