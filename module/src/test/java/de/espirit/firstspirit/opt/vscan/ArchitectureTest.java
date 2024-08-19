package de.espirit.firstspirit.opt.vscan;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.jetbrains.annotations.ApiStatus;

import static com.tngtech.archunit.base.DescribedPredicate.and;
import static com.tngtech.archunit.core.domain.AccessTarget.Predicates.declaredIn;
import static com.tngtech.archunit.core.domain.JavaCall.Predicates.target;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = {ArchitectureTest.class}, importOptions = {ImportOption.Predefined.DoNotIncludeTests.class})
public class ArchitectureTest {

    private static final DescribedPredicate<HasName> FS_CLASS = HasName.Predicates.nameStartingWith("de.espirit");

    @ArchTest
    public static final ArchRule doNotDependOnRuntimeClasses =
            noClasses().should().dependOnClassesThat(and(FS_CLASS, annotatedWith(ApiStatus.Internal.class)));

    @ArchTest
    public static final ArchRule doNotUseDeprecatedClasses =
            noClasses().should().dependOnClassesThat(and(FS_CLASS, annotatedWith(Deprecated.class)));

    @ArchTest
    public static final ArchRule doNotUseDeprecatedMethods =
            noClasses().should().callMethodWhere(target(declaredIn(FS_CLASS).and(annotatedWith(Deprecated.class))));

}
