package org.pitest.cucumber;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;
import org.pitest.util.Preconditions;

import java.util.Collection;

public class CucumberTestFrameworkPlugin implements TestPluginFactory {

    public String description() {
        return "Cucumber with JUnit support";
    }

    public Configuration createTestFrameworkConfiguration(TestGroupConfig config,
                                                          ClassByteArraySource source,
                                                          Collection<String> excludedRunners) {

        return new CucumberJUnitCompatibleConfiguration(config);
    }

    @Override
    public Configuration createTestFrameworkConfiguration(TestGroupConfig config, ClassByteArraySource source, Collection<String> excludedRunners, Collection<String> includedTestMethods) {
        Preconditions.checkNotNull(config);
        return new CucumberJUnitCompatibleConfiguration(config);
    }

    @Override
    public String name() {
        return "Cucumber";
    }

}
