package org.pitest.cucumber;


import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

public class ReporterAdapter implements Reporter {

    private final ResultCollector rc;
    private final Description scenarioDescription;
    private boolean closed;

    public ReporterAdapter(ResultCollector rc, Description scenarioDescription) {
        this.rc = rc;
        this.scenarioDescription = scenarioDescription;
        rc.notifyStart(scenarioDescription);
    }


    public void before(Match match, Result result) {
    }

    public void result(Result result) {
        if (closed) {
            return;
        }
        Throwable error = result.getError();
        if (error != null) {
            rc.notifyEnd(scenarioDescription, error);
        } else if (result == Result.SKIPPED || result == Result.UNDEFINED) {
            rc.notifySkipped(scenarioDescription);
            closed = true;
        } else {
            rc.notifyEnd(scenarioDescription);
        }
    }

    public void after(Match match, Result result) {
    }

    public void match(Match match) {

    }

    public void embedding(String mimeType, byte[] data) {

    }

    public void write(String text) {

    }
}
