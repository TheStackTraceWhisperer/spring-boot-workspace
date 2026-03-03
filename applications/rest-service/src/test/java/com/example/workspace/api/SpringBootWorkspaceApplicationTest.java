package com.example.workspace.api;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class SpringBootWorkspaceApplicationTest {

  @Test
  void main_invokesSpringApplicationRun() {
    try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
      SpringBootWorkspaceApplication.main(new String[]{});

      mocked.verify(() ->
          SpringApplication.run(SpringBootWorkspaceApplication.class, new String[]{}));
    }
  }
}


