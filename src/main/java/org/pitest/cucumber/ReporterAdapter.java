package org.pitest.cucumber;


import cucumber.api.Result;
import cucumber.api.event.EventHandler;
import cucumber.api.event.TestCaseFinished;
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
        Result result = ((TestCaseFinished) event).result;
        Throwable error = result.getError();
        if (error != null) {
            rc.notifyEnd(scenarioDescription, error);
        } else if (result.is(Result.Type.SKIPPED) || result.is(Result.Type.UNDEFINED)) {
            rc.notifySkipped(scenarioDescription);
            closed = true;
        } else {
            rc.notifyEnd(scenarioDescription);
        }
    }

}
