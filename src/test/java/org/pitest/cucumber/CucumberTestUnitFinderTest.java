package org.pitest.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;

class CucumberTestUnitFinderTest {

    private CucumberTestUnitFinder finder;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        this.finder = new CucumberTestUnitFinder();
    }

    @Test
    void should_find_one_test_unit_for_one_feature_available_in_classpath() {
        // given cucumber features in the classpath

        // when
        List<TestUnit> testUnits = finder.findTestUnits(HideFromJUnit.Cornichon.class);

        // then
        assertThat(testUnits).hasSize(1);
        Description description = testUnits.get(0).getDescription();
        assertThat(description.getFirstTestClass()).contains(HideFromJUnit.Cornichon.class.getSimpleName());
        assertThat(description.getName()).containsIgnoringCase("Shopping").containsIgnoringCase("change");
    }

    @Test
    void should_find_one_test_unit_for_one_feature_available_in_classpath_using_subclassed_runner() {
        // given cucumber features in the classpath

        // when
        List<TestUnit> testUnits = finder.findTestUnits(HideFromJUnit.Pepino.class);

        // then
        assertThat(testUnits).hasSize(1);
        Description description = testUnits.get(0).getDescription();
        assertThat(description.getFirstTestClass()).contains(HideFromJUnit.Pepino.class.getSimpleName());
        assertThat(description.getName()).containsIgnoringCase("Shopping").containsIgnoringCase("change");
    }

    @Test
    void should_find_as_many_test_units_as_features_and_examples_available_in_classpath() {
        // given cucumber features in the classpath

        // when
        List<TestUnit> testUnits = finder.findTestUnits(HideFromJUnit.Concombre.class);

        // then
        assertThat(testUnits).hasSize(8);
    }

    @Test
    void should_find_no_test_units_on_a_standard_junit_test() {
        // given this very test class

        // when
        List<TestUnit> testUnits = finder.findTestUnits(getClass());

        // then
        assertThat(testUnits).isEmpty();

    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        @CucumberOptions(features = "classpath:cucumber/examples/java/calculator/shopping.feature")
        private static class Cornichon {
        }

        @RunWith(Cucumber.class)
        @CucumberOptions(features = "classpath:")
        private static class Concombre {
        }

        @RunWith(Gurke.class)
        @CucumberOptions(features = "classpath:cucumber/examples/java/calculator/shopping.feature")
        private static class Pepino {
        }

        private class Gurke extends Cucumber {
            public Gurke(Class<?> clazz) throws InitializationError {
                super(clazz);
            }
        }

    }
}
