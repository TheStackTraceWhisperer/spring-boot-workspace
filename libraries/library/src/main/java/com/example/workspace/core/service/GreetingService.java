package com.example.workspace.core.service;

import com.example.workspace.core.domain.Greeting;
import com.example.workspace.core.repository.GreetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GreetingService {

  private final GreetingRepository greetingRepository;


  @Transactional
  public Greeting create(String name) {
    var greeting = new Greeting("Hello, " + name + "!");
    return greetingRepository.save(greeting);
  }

  @Transactional(readOnly = true)
  public Optional<Greeting> findById(Long id) {
    return greetingRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<Greeting> findAll() {
    return greetingRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Greeting> search(String keyword) {
    return greetingRepository.findByMessageContainingIgnoreCase(keyword);
  }
}

