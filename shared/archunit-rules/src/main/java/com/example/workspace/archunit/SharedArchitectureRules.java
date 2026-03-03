package com.example.workspace.archunit;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.library.GeneralCodingRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Shared ArchUnit rules enforced across all modules.
 *
 * <p>These rules are automatically executed by {@link AbstractArchitectureTest}.
 * Each rule uses {@code allowEmptyShould(true)} only where the package may
 * legitimately not exist in every module (e.g. not all modules have a
 * {@code ..controller..} package). Rules that validate universal constraints
 * (like logging) apply to all classes and do not suppress empty results.
 */
public final class SharedArchitectureRules {

  private SharedArchitectureRules() {
  }

  /**
   * No class should use {@code java.util.logging}. Use SLF4J instead.
   */
  public static void noJavaUtilLogging(JavaClasses importedClasses) {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING
        .check(importedClasses);
  }

  /**
   * Concrete classes in a {@code ..service..} package must have a name
   * ending with {@code Service} or {@code ServiceImpl}.
   */
  public static void serviceClassesShouldBeNamedService(JavaClasses importedClasses) {
    classes()
        .that().resideInAPackage("..service..")
        .and().areNotInterfaces()
        .and().areNotAnonymousClasses()
        .and().areNotMemberClasses()
        .should().haveSimpleNameEndingWith("Service")
        .orShould().haveSimpleNameEndingWith("ServiceImpl")
        .allowEmptyShould(true)
        .because("service classes should follow the naming convention *Service or *ServiceImpl")
        .check(importedClasses);
  }

  /**
   * Concrete classes in a {@code ..repository..} package must have a name
   * ending with {@code Repository} or {@code RepositoryImpl}.
   */
  public static void repositoryClassesShouldBeNamedRepository(JavaClasses importedClasses) {
    classes()
        .that().resideInAPackage("..repository..")
        .and().areNotInterfaces()
        .and().areNotAnonymousClasses()
        .and().areNotMemberClasses()
        .should().haveSimpleNameEndingWith("Repository")
        .orShould().haveSimpleNameEndingWith("RepositoryImpl")
        .allowEmptyShould(true)
        .because("repository classes should follow the naming convention *Repository or *RepositoryImpl")
        .check(importedClasses);
  }

  /**
   * Classes in {@code ..service..} packages must not depend on classes in
   * {@code ..controller..} packages. The service layer should be independent
   * of the presentation layer.
   */
  public static void servicesShouldNotDependOnControllers(JavaClasses importedClasses) {
    noClasses()
        .that().resideInAPackage("..service..")
        .should().dependOnClassesThat().resideInAPackage("..controller..")
        .allowEmptyShould(true)
        .because("services must not depend on controllers — the dependency should be the other way around")
        .check(importedClasses);
  }

  /**
   * Classes in {@code ..domain..}, {@code ..entity..}, or {@code ..model..}
   * packages must not depend on Spring framework classes.
   */
  public static void domainObjectsShouldNotDependOnSpring(JavaClasses importedClasses) {
    noClasses()
        .that().resideInAnyPackage("..domain..", "..entity..", "..model..")
        .should().dependOnClassesThat().resideInAPackage("org.springframework..")
        .allowEmptyShould(true)
        .because("domain objects should be framework-agnostic")
        .check(importedClasses);
  }

  /**
   * Every class in an {@code ..exception..} package must extend {@link Throwable}.
   */
  public static void exceptionPackageShouldOnlyContainExceptions(JavaClasses importedClasses) {
    classes()
        .that().resideInAPackage("..exception..")
        .should().beAssignableTo(Throwable.class)
        .allowEmptyShould(true)
        .because("only exception classes belong in the exception package")
        .check(importedClasses);
  }

  /**
   * Detects self-invocation of AOP-annotated methods within the same class.
   *
   * <p>When a Spring bean calls one of its own methods that is annotated with
   * {@code @Transactional}, {@code @Cacheable}, {@code @CachePut},
   * {@code @CacheEvict}, or {@code @Async}, the call bypasses the Spring
   * proxy and the AOP behavior is silently lost.
   *
   * <p>The fix is to extract the annotated method into a separate bean.
   */
  public static void noSelfInvocationOfAopAnnotatedMethods(JavaClasses importedClasses) {
    List<String> aopAnnotations = List.of(
        "org.springframework.transaction.annotation.Transactional",
        "org.springframework.cache.annotation.Cacheable",
        "org.springframework.cache.annotation.CachePut",
        "org.springframework.cache.annotation.CacheEvict",
        "org.springframework.scheduling.annotation.Async"
    );

    List<String> violations = new ArrayList<>();

    for (JavaClass javaClass : importedClasses) {
      for (JavaMethod caller : javaClass.getMethods()) {
        for (JavaMethodCall call : caller.getMethodCallsFromSelf()) {
          if (!call.getTargetOwner().getName().equals(javaClass.getName())) {
            continue;
          }
          Optional<JavaMethod> resolved = call.getTarget().resolveMember();
          if (resolved.isEmpty()) {
            continue;
          }
          JavaMethod target = resolved.get();
          for (String annotation : aopAnnotations) {
            if (target.isAnnotatedWith(annotation)) {
              String shortAnnotation = annotation.substring(annotation.lastIndexOf('.') + 1);
              violations.add(String.format(
                  "%s.%s() calls @%s method %s() in the same class (line %d). "
                      + "This bypasses the proxy — extract %s() into a separate bean.",
                  javaClass.getSimpleName(), caller.getName(),
                  shortAnnotation, target.getName(),
                  call.getLineNumber(), target.getName()));
            }
          }
        }
      }
    }

    if (!violations.isEmpty()) {
      throw new AssertionError(
          "Self-invocation of AOP-annotated methods detected:\n  - "
              + String.join("\n  - ", violations));
    }
  }
}


