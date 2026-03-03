package com.example.workspace.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Base class for ArchUnit architecture tests.
 *
 * <p>Automatically runs all shared architecture rules from {@link SharedArchitectureRules}
 * against the production classes in the module's base package. Subclasses only need
 * to declare the package to scan — all standard rules execute automatically.
 *
 * <p>Subclasses may add module-specific rules as additional {@code @Test} methods.
 *
 * <p>Example:
 * <pre>
 * class MyModuleArchitectureTest extends AbstractArchitectureTest {
 *
 *   &#64;Override
 *   protected String getBasePackage() {
 *     return "com.example.mymodule";
 *   }
 *
 *   // all shared rules run automatically
 *
 *   &#64;Test
 *   void module_specific_rule() {
 *     // additional rules using 'classes' field
 *   }
 * }
 * </pre>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractArchitectureTest {

  protected JavaClasses classes;

  @BeforeAll
  public void importClasses() {
    classes = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages(getBasePackage());
  }

  /**
   * Returns the root package of the module's production code.
   * All subpackages will be scanned recursively.
   *
   * @return the base package, e.g. {@code "com.example.workspace.core"}
   */
  protected abstract String getBasePackage();

  @Test
  void no_classes_should_use_java_util_logging() {
    SharedArchitectureRules.noJavaUtilLogging(classes);
  }

  @Test
  void services_should_not_depend_on_controllers() {
    SharedArchitectureRules.servicesShouldNotDependOnControllers(classes);
  }

  @Test
  void classes_in_service_package_should_be_named_service() {
    SharedArchitectureRules.serviceClassesShouldBeNamedService(classes);
  }

  @Test
  void classes_in_repository_package_should_be_named_repository() {
    SharedArchitectureRules.repositoryClassesShouldBeNamedRepository(classes);
  }

  @Test
  void domain_objects_should_not_depend_on_spring() {
    SharedArchitectureRules.domainObjectsShouldNotDependOnSpring(classes);
  }

  @Test
  void exception_package_should_only_contain_exceptions() {
    SharedArchitectureRules.exceptionPackageShouldOnlyContainExceptions(classes);
  }

  @Test
  void no_self_invocation_of_aop_annotated_methods() {
    SharedArchitectureRules.noSelfInvocationOfAopAnnotatedMethods(classes);
  }
}


