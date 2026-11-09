package com.ticketflow.catalog.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Guards the hexagonal (ports & adapters) structure of the service.
 * A failing rule means a dependency points the wrong way — fix the design, not the rule.
 */
@AnalyzeClasses(packages = "com.ticketflow.catalog", importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule dependenciesPointInwards = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("web", "..adapter.in.web..")
            .adapter("persistence", "..adapter.out.persistence..")
            .withOptionalLayers(true)
            // `config` is the composition root: the single place allowed to wire all layers together.
            .ignoreDependency(resideInAPackage("..config.."), alwaysTrue());

    @ArchTest
    static final ArchRule coreIsFrameworkFree = noClasses()
            .that().resideInAnyPackage("..domain..", "..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "tools.jackson..", "com.fasterxml.jackson..", "org.bson..")
            .because("the domain and application core must not depend on frameworks");
}
