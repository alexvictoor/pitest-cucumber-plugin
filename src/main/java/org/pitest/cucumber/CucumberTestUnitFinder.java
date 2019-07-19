package org.pitest.cucumber;

import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.RuntimeOptions;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Log;

import cucumber.api.junit.Cucumber;
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
        List<TestUnit> result = new ArrayList<>();
        RunWith annotation = junitTestClass.getAnnotation(RunWith.class);
        if (annotation!= null && Cucumber.class.isAssignableFrom(annotation.value())) {
            RuntimeOptions runtimeOptions = new CucumberOptionsAnnotationParser().parse(junitTestClass).build();
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
		}
		return result;
	}
}
