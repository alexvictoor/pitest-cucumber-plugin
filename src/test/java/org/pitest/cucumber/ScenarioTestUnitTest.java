package org.pitest.cucumber;

import cucumber.api.junit.Cucumber;
import cucumber.runner.EventBus;
import cucumber.runner.Runner;
import cucumber.runtime.Runtime;
import gherkin.events.PickleEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioTestUnitTest {

    @Mock
    PickleEvent scenario;

    @Mock
    Runtime runtime;
    
    @Mock
    Runner runner;
    
    @Mock
    EventBus eventBus;

    @Mock
    private ResultCollector resultCollector;

    @Before
    public void setUp() throws Exception {
        when(runtime.getRunner()).thenReturn(runner);
        when(runtime.getEventBus()).thenReturn(eventBus);
    }

    @Test
    public void should_run_scenario_and_call_collector_when_ran() {
        // given
        ScenarioTestUnit testUnit = new ScenarioTestUnit(new Description("", HideFromJUnit.Concombre.class), scenario, runtime);

        // when
        testUnit.execute(resultCollector);

        // then
        verify(runner, times(1)).runPickle(scenario);
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        private static class Concombre {
        }

    }
}
