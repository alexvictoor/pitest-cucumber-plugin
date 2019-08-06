package org.pitest.cucumber;

import cucumber.api.SnippetType;
import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Log;

import cucumber.runner.EventBus;
import cucumber.runner.RunnerSupplier;
import cucumber.runner.SingletonRunnerSupplier;
import cucumber.runner.TimeService;
import cucumber.runner.TimeServiceEventBus;
import cucumber.runtime.BackendModuleBackendSupplier;
import cucumber.runtime.BackendSupplier;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.FeaturePathFeatureSupplier;
import cucumber.runtime.FeatureSupplier;
import cucumber.runtime.filter.Filters;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.FeatureLoader;
import gherkin.events.PickleEvent;

public class CucumberTestUnitFinder implements TestUnitFinder {

    public List<TestUnit> findTestUnits(Class<?> junitTestClass) {

        if (hasACucumberAnnotation(junitTestClass)) {

            List<TestUnit> result = new ArrayList<>();

            RuntimeOptions runtimeOptions = new CucumberOptionsAnnotationParser()
                .withOptionsProvider(new CustomProvider())
                .parse(junitTestClass)
                .build();

            ClassLoader classLoader = junitTestClass.getClassLoader();
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
            FeatureLoader featureLoader = new FeatureLoader(resourceLoader);
            FeatureSupplier featureSupplier = new FeaturePathFeatureSupplier(featureLoader, runtimeOptions);
            final List<CucumberFeature> cucumberFeatures = featureSupplier.get();
            final Filters filters = new Filters(runtimeOptions);
            EventBus eventBus = new TimeServiceEventBus(TimeService.SYSTEM);
            BackendSupplier backendSupplier = new BackendModuleBackendSupplier(resourceLoader, classFinder, runtimeOptions);
            RunnerSupplier runnerSupplier = new SingletonRunnerSupplier(runtimeOptions, eventBus, backendSupplier);
            for (CucumberFeature feature : cucumberFeatures) {
                Log.getLogger().fine("Found feature \"" + feature.getGherkinFeature().getFeature().getName() + "\"");
                List<PickleEvent> pickles = feature.getPickles();
                for (PickleEvent pickle : pickles) {
                    if (!filters.matchesFilters(pickle)) continue;
                    Description description = new Description(
                        feature.getGherkinFeature().getFeature().getName() + " : " + pickle.pickle.getName(),
                        junitTestClass);
                    Log.getLogger().fine("Found \"" + description.getName() + "\"");
                    result.add(new ScenarioTestUnit(description, pickle, runnerSupplier, eventBus));
                }
            }

            return result;

        }

        return Collections.emptyList();
    }

    private boolean hasACucumberAnnotation(Class<?> junitTestClass) {
        RunWith annotation = junitTestClass.getAnnotation(RunWith.class);
        return annotation != null && Cucumber.class.isAssignableFrom(annotation.value());
    }

    private class CustomProvider implements CucumberOptionsAnnotationParser.OptionsProvider {
        @Override
        public CucumberOptionsAnnotationParser.CucumberOptions getOptions(Class<?> clazz) {
            // this is ok since up to Cucumber 4.7.1, il will fallback on cucumber.api.CucumberOptions
            // @see io.cucumber.core.options.CucumberOptionsAnnotationParser (l.41)
            final io.cucumber.junit.CucumberOptions annotation = clazz.getAnnotation(io.cucumber.junit.CucumberOptions.class);
            if (annotation == null) {
                return null;
            }
            return new CustomCucumberOptions(annotation);
        }
    }

    private class CustomCucumberOptions implements CucumberOptionsAnnotationParser.CucumberOptions {
        private final CucumberOptions annotation;

        public CustomCucumberOptions(CucumberOptions annotation) {
            this.annotation = annotation;
        }

        @Override
        public boolean dryRun() {
            return annotation.dryRun();
        }

        @Override
        public boolean strict() {
            return annotation.strict();
        }

        @Override
        public String[] features() {
            return annotation.features();
        }

        @Override
        public String[] glue() {
            return annotation.glue();
        }

        @Override
        public String[] extraGlue() {
            return annotation.extraGlue();
        }

        @Override
        public String[] tags() {
            return annotation.tags();
        }

        @Override
        public String[] plugin() {
            return annotation.plugin();
        }

        @Override
        public boolean monochrome() {
            return annotation.monochrome();
        }

        @Override
        public String[] name() {
            return annotation.name();
        }

        @Override
        public SnippetType snippets() {
            CucumberOptions.SnippetType snippets = annotation.snippets();
            return SnippetType.fromString(snippets.name());
        }

        @Override
        public String[] junit() {
            return annotation.junit();
        }
    }
}
