package org.pitest.cucumber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import cucumber.examples.java.calculator.Cornichon;
import cucumber.examples.java.calculator.DateCalculator;
import deprecated.cucumber.example.java.calculator.DeprecatedCornichon;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @ParameterizedTest
    @ValueSource( classes = { DeprecatedCornichon.class, Cornichon.class } )
    void should_run_scenarios_successfully(Class<?> clazz) {
        // given
        TestUnit firstTest = getScenarioTestUnit(clazz);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, atLeastOnce()).notifyEnd(description);
    }

    @ParameterizedTest
    @ValueSource( classes = { DeprecatedCornichon.class, Cornichon.class } )
    void should_detect_scenario_failure(Class<?> clazz) {
        // given
        TestUnit firstTest = getScenarioTestUnit(clazz);
        DateCalculator.failMode.set(true);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, times(1)).notifyEnd(any(Description.class), any(Throwable.class));
    }

    @ParameterizedTest
    @ValueSource( classes = { HideFromJUnit.DeprecatedCornichon.class, HideFromJUnit.Cornichon.class } )
    void should_detect_skipped_scenario(Class<?> clazz) {
        // given
        CucumberTestUnitFinder finder = new CucumberTestUnitFinder();
        List<TestUnit> testUnits = finder.findTestUnits(clazz);
        TestUnit firstTest = testUnits.get(0);

        // when
        firstTest.execute(resultCollector);

        // then
        Description description = firstTest.getDescription();
        verify(resultCollector, times(1)).notifyStart(description);
        verify(resultCollector, times(1)).notifySkipped(description);
    }

    private TestUnit getScenarioTestUnit(Class<?> clazz) {
        CucumberTestUnitFinder finder = new CucumberTestUnitFinder();
        List<TestUnit> testUnits = finder.findTestUnits(clazz);
        return testUnits.get(0);
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        @CucumberOptions(features = "classpath:cucumber/examples/java/calculator/date_calculator.feature")
        private static class Cornichon {
        }

        @RunWith(cucumber.api.junit.Cucumber.class)
        @cucumber.api.CucumberOptions(features = "classpath:cucumber/examples/java/calculator/date_calculator.feature")
        private static class DeprecatedCornichon {
        }

    }
}
