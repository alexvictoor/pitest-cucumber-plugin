package org.pitest.cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.*;
import org.junit.runner.RunWith;
import org.pitest.testapi.TestUnit;
import org.pitest.testapi.TestUnitFinder;
import org.pitest.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CucumberTestUnitFinder implements TestUnitFinder {

    public List<TestUnit> findTestUnits(Class<?> junitTestClass) {
        List<TestUnit> result = new ArrayList<TestUnit>();
        RunWith annotation = junitTestClass.getAnnotation(RunWith.class);
        if (annotation!= null && Cucumber.class.equals(annotation.value())) {
            RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(junitTestClass, new Class[]{CucumberOptions.class, Cucumber.Options.class});
            RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
            ClassLoader classLoader = junitTestClass.getClassLoader();
            ResourceLoader resourceLoader = new MultiLoader(classLoader);
            final List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);
            for (CucumberFeature feature : cucumberFeatures) {
                Log.getLogger().fine("Found feature \"" + feature.getGherkinFeature().getName() + "\"");
                List<CucumberTagStatement> featureElements = feature.getFeatureElements();
                for (CucumberTagStatement element : featureElements) {
                    if (element instanceof CucumberScenario) {
                        CucumberScenario scenario = (CucumberScenario) element;
                        Log.getLogger().fine("Found \"" + scenario.getVisualName() + "\"");
                        ScenarioTestUnit testUnit = new ScenarioTestUnit(junitTestClass, scenario);
                        result.add(testUnit);
                    } else if (element instanceof CucumberScenarioOutline) {
                        CucumberScenarioOutline scenarioOutline = (CucumberScenarioOutline) element;
                        Log.getLogger().fine("Found \"" + scenarioOutline.getVisualName() + "\"");
                        for (CucumberExamples examples : scenarioOutline.getCucumberExamplesList()) {
                            for (CucumberScenario scenario : examples.createExampleScenarios()) {
                                ScenarioTestUnit testUnit = new ScenarioTestUnit(junitTestClass, scenario);
                                result.add(testUnit);
                            }
                        }

                    } else {
                        Log.getLogger().warning("Ignoring unknown cucumber tag statement " + element.getVisualName());
                    }
                }
            }
        }
        return result;
    }
}
