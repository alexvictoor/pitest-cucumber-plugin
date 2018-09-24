package org.pitest.cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.examples.java.calculator.Cornichon;
import cucumber.examples.java.calculator.DateCalculator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationTest {

    @Mock
    private ResultCollector resultCollector;

    @Before
    public void setUp() throws Exception {
        DateCalculator.failMode.set(null);
    }

    @Test
    public void should_run_scenarios_successfully() throws Exception {
        // given
        TestUnit firstTest = getScenarioTestUnit();

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, atLeastOnce()).notifyEnd(description);
    }

    @Test
    public void should_detect_scenario_failure() throws Exception {
        // given
        TestUnit firstTest = getScenarioTestUnit();
        DateCalculator.failMode.set(true);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, times(1)).notifyEnd(any(Description.class), any(Throwable.class));
    }

    @Test
    public void should_detect_skipped_scenario() throws Exception {
        // given
        CucumberTestUnitFinder finder = new CucumberTestUnitFinder();
        List<TestUnit> testUnits = finder.findTestUnits(HideFromJUnit.Cornichon.class);
        TestUnit firstTest = testUnits.get(0);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, times(1)).notifySkipped(description);
    }

    private TestUnit getScenarioTestUnit() {
        CucumberTestUnitFinder finder = new CucumberTestUnitFinder();
        List<TestUnit> testUnits = finder.findTestUnits(Cornichon.class);
        return testUnits.get(0);
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        @CucumberOptions(features = "classpath:cucumber/examples/java/calculator/date_calculator.feature")
        private static class Cornichon {
        }
    }
}
