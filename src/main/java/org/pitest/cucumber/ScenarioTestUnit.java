package org.pitest.cucumber;


import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

public class ScenarioTestUnit implements TestUnit {

    private static final Logger LOGGER = Log.getLogger();
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
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(junitTestClass);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Runtime runtime = new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
        Reporter reporter = new ReporterAdapter(rc, getDescription());
        LOGGER.fine("Executing cucumber \"" + scenario.getVisualName() + "\"");
        scenario.run(nullFormatter(), reporter, runtime);
    }

    private Formatter nullFormatter() {
        return (Formatter) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Formatter.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
    }

    public Description getDescription() {
        return new Description(scenario.getGherkinModel().getId(), junitTestClass);
    }
}
