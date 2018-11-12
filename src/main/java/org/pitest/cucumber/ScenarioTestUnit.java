package org.pitest.cucumber;


import cucumber.api.event.EventHandler;
import cucumber.api.event.TestCaseFinished;
import cucumber.runner.EventBus;
import cucumber.runner.RunnerSupplier;
import gherkin.events.PickleEvent;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

import java.util.logging.Logger;

public class ScenarioTestUnit implements TestUnit {

    private static final Logger LOGGER = Log.getLogger();
    private final PickleEvent scenario;
    private final RunnerSupplier runnerSupplier;
    private final EventBus eventBus;

    private final Description description;

    public ScenarioTestUnit(Description description, PickleEvent scenario, RunnerSupplier runnerSupplier, EventBus eventBus) {
        this.description = description;
        this.scenario = scenario;
        this.runnerSupplier = runnerSupplier;
        this.eventBus = eventBus;
    }

    @Override
    public void execute(ResultCollector rc) {
        EventHandler<TestCaseFinished> handler = new ReporterAdapter(rc, getDescription());
        LOGGER.fine("Executing cucumber \"" + description.getName() + "\"");
        eventBus.registerHandlerFor(TestCaseFinished.class, handler);
        runnerSupplier.get().runPickle(scenario);
        eventBus.removeHandlerFor(TestCaseFinished.class, handler);
    }

    public Description getDescription() {
        return description;
    }
}
