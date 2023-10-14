package org.pitest.cucumber;

import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.snippets.SnippetType;
import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.filter.Filters;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.runtime.*;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.runner.RunWith;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Log;

public class CucumberTestUnitFinder implements TestUnitFinder {

    public List<TestUnit> findTestUnits(Class<?> junitTestClass) {

        if (hasACucumberAnnotation(junitTestClass)) {
//            System.err.println("found cuce test "+junitTestClass.getName());

            List<TestUnit> result = new ArrayList<>();

            RuntimeOptions runtimeOptions = new CucumberOptionsAnnotationParser()
                .withOptionsProvider(new CustomProvider())
                .parse(junitTestClass)
                .build();

            TimeServiceEventBus bus = new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID);
            FeatureParser parser = new FeatureParser(bus::generateId);

            FeatureSupplier featureSupplier = new FeaturePathFeatureSupplier(junitTestClass::getClassLoader, runtimeOptions, parser);
            final List<Feature> cucumberFeatures = featureSupplier.get();
            final Filters filters = new Filters(runtimeOptions);
            EventBus eventBus = new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID);
            ObjectFactoryServiceLoader objectFactoryServiceLoader = new ObjectFactoryServiceLoader(junitTestClass::getClassLoader,runtimeOptions);
            ObjectFactorySupplier objectFactorySupplier = new ThreadLocalObjectFactorySupplier(objectFactoryServiceLoader);
            BackendSupplier backendSupplier = new BackendServiceLoader(junitTestClass::getClassLoader, objectFactorySupplier);
            RunnerSupplier runnerSupplier = new SingletonRunnerSupplier(runtimeOptions, eventBus, backendSupplier, objectFactorySupplier );
            for (Feature feature : cucumberFeatures) {
                Log.getLogger().fine("Found feature \"" + feature.getName() + "\"");
                List<Pickle> pickles = feature.getPickles();
                for (Pickle pickle : pickles) {
                    if (!filters.test(pickle)) continue;
                    Description description = new Description(
                        feature.getName() + " : " + pickle.getName(),
                        junitTestClass);
                    Log.getLogger().fine("Found \"" + description.getName() + "\"");
                    result.add(new ScenarioTestUnit(description, pickle, runnerSupplier, eventBus));
                }
            }
//            System.err.println(" --- res "+result.toString());

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
        public String tags() {
            return annotation.tags();
        }

        @Override
        public String[] plugin() {
            return annotation.plugin();
        }

        @Override
        public boolean publish() {
            return annotation.publish();
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
            switch (annotation.snippets()) {
                case UNDERSCORE:
                    return SnippetType.UNDERSCORE;
                case CAMELCASE:
                    return SnippetType.CAMELCASE;
                default:
                    throw new IllegalArgumentException("" + annotation.snippets());
            }
        }

        @Override
        public Class<? extends ObjectFactory> objectFactory() {
            return (annotation.objectFactory().getCanonicalName().equals("io.cucumber.junit.NoObjectFactory")) ? null : annotation.objectFactory();
        }
    }
}
