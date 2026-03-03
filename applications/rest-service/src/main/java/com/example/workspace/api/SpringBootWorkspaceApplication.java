package com.example.workspace.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// scanBasePackages is what makes the `library` classes available as Spring beans
// You don't need to do this if you use an @AutoConfiguration
@SpringBootApplication(scanBasePackages = "com.example.workspace")
// This is also unnecessary had we used an @AutoConfiguration and a Registrar
@EntityScan("com.example.workspace")
// Again, unnecessary if we used an @AutoConfiguration
@EnableJpaRepositories("com.example.workspace")
public class SpringBootWorkspaceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootWorkspaceApplication.class, args);
  }

}
