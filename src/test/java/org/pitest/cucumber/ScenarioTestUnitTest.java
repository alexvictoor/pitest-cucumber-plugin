package org.pitest.cucumber;

import cucumber.api.junit.Cucumber;
import cucumber.runtime.Runtime;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.testapi.ResultCollector;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioTestUnitTest {

    @Mock
    CucumberScenario scenario;

    @Mock
    private ResultCollector resultCollector;

    @Test
    public void should_run_scenario_and_call_collector_when_ran() {
        // given
        ScenarioTestUnit testUnit = new ScenarioTestUnit(HideFromJUnit.Concombre.class, scenario);

        // when
        testUnit.execute(getClass().getClassLoader(), resultCollector);

        // then
        verify(scenario, times(1)).run(any(Formatter.class), any(Reporter.class), any(Runtime.class));
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        private static class Concombre {
        }

    }
}