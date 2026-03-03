package com.example.workspace.application;

import com.example.workspace.archunit.AbstractArchitectureTest;

/**
 * Architecture tests for the application module.
 *
 * All shared rules from {@link AbstractArchitectureTest} run automatically.
 * Add module-specific rules as additional {@code @Test} methods here.
 */
class ApplicationArchitectureTest extends AbstractArchitectureTest {

  @Override
  protected String getBasePackage() {
    return "com.example.workspace.api";
  }
}

