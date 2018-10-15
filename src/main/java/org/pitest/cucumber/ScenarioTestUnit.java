package org.pitest.cucumber;


import cucumber.api.event.EventHandler;
import cucumber.api.event.TestCaseFinished;
import cucumber.runner.EventBus;
import cucumber.runtime.Runtime;
import gherkin.events.PickleEvent;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

import java.util.logging.Logger;

public class ScenarioTestUnit implements TestUnit {

    private static final Logger LOGGER = Log.getLogger();
    private final PickleEvent scenario;
    private final Runtime runtime;

    private final Description description;

    public ScenarioTestUnit(Description description, PickleEvent scenario, Runtime runtime) {
        this.description = description;
        this.scenario = scenario;
        this.runtime = runtime;
    }

    @Override
    public void execute(ResultCollector rc) {
        EventHandler<TestCaseFinished> handler = new ReporterAdapter(rc, getDescription());
        LOGGER.fine("Executing cucumber \"" + description.getName() + "\"");
        EventBus eventBus = runtime.getEventBus();
        eventBus.registerHandlerFor(TestCaseFinished.class, handler);
        runtime.getRunner().runPickle(scenario);
    }

    public Description getDescription() {
        return description;
    }
}
