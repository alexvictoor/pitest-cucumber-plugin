package org.pitest.cucumber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.examples.java.calculator.Cornichon;
import cucumber.examples.java.calculator.DateCalculator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;

@ExtendWith(MockitoExtension.class)
class IntegrationTest {

    @Mock
    private ResultCollector resultCollector;

    @BeforeEach
    void setUp() {
        DateCalculator.failMode.set(null);
    }

    @Test
    void should_run_scenarios_successfully() {
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
    void should_detect_scenario_failure() {
        // given
        TestUnit firstTest = getScenarioTestUnit();
        DateCalculator.failMode.set(true);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, times(1)).notifyEnd(any(Description.class));
    }

    @Test
    void should_detect_skipped_scenario() {
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
