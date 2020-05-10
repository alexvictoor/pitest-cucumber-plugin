package org.pitest.cucumber;


import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.runtime.RunnerSupplier;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.TestCaseFinished;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.util.Log;

import java.util.logging.Logger;

public class ScenarioTestUnit implements TestUnit {

    private static final Logger LOGGER = Log.getLogger();
    private final Pickle scenario;
    private final RunnerSupplier runnerSupplier;
    private final EventBus eventBus;

    private final Description description;

    public ScenarioTestUnit(Description description, Pickle scenario, RunnerSupplier runnerSupplier, EventBus eventBus) {
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
