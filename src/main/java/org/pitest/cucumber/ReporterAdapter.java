package org.pitest.cucumber;

import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseFinished;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

public class ReporterAdapter implements EventHandler<TestCaseFinished> {

    private final ResultCollector rc;
    private final Description scenarioDescription;
    private boolean closed;

    public ReporterAdapter(ResultCollector rc, Description scenarioDescription) {
        this.rc = rc;
        this.scenarioDescription = scenarioDescription;
        rc.notifyStart(scenarioDescription);
    }


    @Override
    public void receive(TestCaseFinished event) {
        if (closed) {
            return;
        }
        Result result = event.getResult();
        Throwable error = result.getError();
        if (error != null) {
            rc.notifyEnd(scenarioDescription, error);
        } else if (result.getStatus().is(Status.SKIPPED) || result.getStatus().is(Status.UNDEFINED)) {
            rc.notifySkipped(scenarioDescription);
            closed = true;
        } else {
            rc.notifyEnd(scenarioDescription);
        }
    }

}
