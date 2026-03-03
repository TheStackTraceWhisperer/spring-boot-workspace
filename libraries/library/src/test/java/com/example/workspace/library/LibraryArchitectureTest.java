package com.example.workspace.library;

import com.example.workspace.archunit.AbstractArchitectureTest;

/**
 * Architecture tests for the library module.
 *
 * All shared rules from {@link AbstractArchitectureTest} run automatically.
 * Add module-specific rules as additional {@code @Test} methods here.
 */
class LibraryArchitectureTest extends AbstractArchitectureTest {

  @Override
  protected String getBasePackage() {
    return "com.example.workspace.core";
  }
}

