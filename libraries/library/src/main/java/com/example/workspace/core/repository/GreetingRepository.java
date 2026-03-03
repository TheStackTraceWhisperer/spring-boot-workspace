package com.example.workspace.core.repository;

import com.example.workspace.core.domain.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GreetingRepository extends JpaRepository<Greeting, Long> {

  List<Greeting> findByMessageContainingIgnoreCase(String keyword);
}

