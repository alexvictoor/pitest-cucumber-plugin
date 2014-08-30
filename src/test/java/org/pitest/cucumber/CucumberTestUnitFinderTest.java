package org.pitest.cucumber;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.pitest.cucumber.CucumberTestUnitFinder;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CucumberTestUnitFinderTest {

    private CucumberTestUnitFinder testee;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.testee = new CucumberTestUnitFinder();
    }

    @Test
    public void testName() throws Exception {
        List<TestUnit> testUnits = testee.findTestUnits(HideFromJUnit.Cornichon.class);
        System.out.println(testUnits.size());
        assertEquals(8, testUnits.size());
        for (TestUnit testUnit : testUnits) {
            System.out.println(testUnit.getDescription());
            testUnit.execute(getClass().getClassLoader(), mock(ResultCollector.class));
        }
    }

    private static class HideFromJUnit {

        @RunWith(Cucumber.class)
        @CucumberOptions(format = "json:target/cucumber-report.json", dotcucumber = ".cucumber")
        private static class Cornichon {
        }

    }
}
