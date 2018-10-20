package org.pitest.cucumber;


import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Log;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberFeature;
import gherkin.events.PickleEvent;

public class CucumberTestUnitFinder implements TestUnitFinder {

    public List<TestUnit> findTestUnits(Class<?> junitTestClass) {
        List<TestUnit> result = new ArrayList<TestUnit>();
        RunWith annotation = junitTestClass.getAnnotation(RunWith.class);
        if (annotation!= null && Cucumber.class.isAssignableFrom(annotation.value())) {
            RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(junitTestClass);
            RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
            ClassLoader classLoader = junitTestClass.getClassLoader();
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
            Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
            final List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader, runtime.getEventBus());
            for (CucumberFeature feature : cucumberFeatures) {
                Log.getLogger().fine("Found feature \"" + feature.getGherkinFeature().getFeature().getName() + "\"");
                List<PickleEvent> pickles = runtime.compileFeature(feature);
                for (PickleEvent pickle : pickles) {
                    Description description = new Description(
							feature.getGherkinFeature().getFeature().getName() + " : " + pickle.pickle.getName(),
							junitTestClass);
                    Log.getLogger().fine("Found \"" + description.getName() + "\"");
                    result.add(new ScenarioTestUnit(description, pickle, runtime));
				}
			}
		}
		return result;
	}
}
