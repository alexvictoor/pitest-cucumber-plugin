package org.pitest.cucumber;

import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.testapi.Configuration;
import org.pitest.testapi.TestGroupConfig;
import org.pitest.testapi.TestPluginFactory;

public class CucumberTestFrameworkPlugin  implements TestPluginFactory {

    public String description() {
        return "Cucumber with JUnit support";
    }

    public Configuration createTestFrameworkConfiguration(TestGroupConfig config,
                                                          ClassByteArraySource source) {

        return new CucumberJUnitCompatibleConfiguration(config);
    }

}
