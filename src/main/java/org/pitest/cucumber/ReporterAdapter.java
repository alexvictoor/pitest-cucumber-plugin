package org.pitest.cucumber;


import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

public class ReporterAdapter implements Reporter {

    private final ResultCollector rc;
    private final Description scenarioDescription;

    public ReporterAdapter(ResultCollector rc, Description scenarioDescription) {
        this.rc = rc;
        this.scenarioDescription = scenarioDescription;
    }


    public void before(Match match, Result result) {
        rc.notifyStart(scenarioDescription);
    }

    public void result(Result result) {
        Throwable error = result.getError();
        if (error != null) {
            rc.notifyEnd(scenarioDescription, error);

        } else if (result == Result.SKIPPED) {
            rc.notifySkipped(scenarioDescription);
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
