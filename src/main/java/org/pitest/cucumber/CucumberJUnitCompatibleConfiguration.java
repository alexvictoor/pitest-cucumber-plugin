package org.pitest.cucumber;


import org.pitest.junit.CompoundTestUnitFinder;
import org.pitest.junit.JUnitCompatibleConfiguration;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestUnitFinder;

import java.util.List;

import static java.util.Arrays.asList;

public class CucumberJUnitCompatibleConfiguration extends JUnitCompatibleConfiguration {

    public CucumberJUnitCompatibleConfiguration(TestGroupConfig config) {
        super(config);
    }

    @Override
    public TestUnitFinder testUnitFinder() {
        List<TestUnitFinder> finders
                = asList(new CucumberTestUnitFinder(), super.testUnitFinder());
        return new CompoundTestUnitFinder(finders);
    }
}
