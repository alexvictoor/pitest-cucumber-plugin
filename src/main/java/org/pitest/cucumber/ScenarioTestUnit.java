package org.pitest.cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.*;
import cucumber.runtime.Runtime;
import cucumber.runtime.formatter.FormatterFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;

public class ScenarioTestUnit implements TestUnit {

    private final CucumberScenario scenario;

    private final Class<?> junitTestClass;

    public ScenarioTestUnit(Class<?> junitTestClass, CucumberScenario scenario) {
        this.junitTestClass = junitTestClass;
        this.scenario = scenario;
    }

    public void execute(ClassLoader classLoader, ResultCollector rc) {
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        // TODO threadlocal runtime cache using junitTestClass as a key
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(junitTestClass, new Class[]{CucumberOptions.class, Cucumber.Options.class});
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        long start = System.currentTimeMillis();
        Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
        long end = System.currentTimeMillis();
        Formatter nullFormater = new FormatterFactory().create("null");
        Reporter reporter = new ReporterAdapter(rc, getDescription());
        System.out.println("Prep needs " + (end-start) + "ms");
        scenario.run(nullFormater, reporter, runtime);
    }

    public Description getDescription() {
        return new Description(scenario.getGherkinModel().getId(), junitTestClass);
    }
}
