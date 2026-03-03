package com.example.workspace.core.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "greeting")
@Schema(description = "A greeting entity")
public class Greeting {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Schema(description = "Unique identifier", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
  private Long id;

  @Column(nullable = false)
  @Schema(description = "Greeting message", example = "Hello, World!")
  private String message;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Schema(description = "Timestamp when the greeting was created", accessMode = Schema.AccessMode.READ_ONLY)
  private Instant createdAt;

  protected Greeting() {
  }

  public Greeting(String message) {
    this.message = message;
    this.createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}

