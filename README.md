[![Build Status](https://travis-ci.org/alexvictoor/pitest-cucumber-plugin.svg?branch=master)](https://travis-ci.org/alexvictoor/pitest-cucumber-plugin)

Cucumber PIT integration plugin
========================

Out of the box [mutation testing tool PIT](http://pitest.org) does not take in account cucumber features and scenarios to kill mutants in your code.  
The purpose of this plugin is to make PIT use your cucumber scenarios the same way it use already your regular junit tests.

Limitations
------------
Today, PIT plugins require maven or [Gradle](https://github.com/szpak/gradle-pitest-plugin). You cannot for the moment use this plugin with the PIT command line interface.

Usage
------
### Maven

Just add this plugin as a maven dependency of PIT maven plugin. Below an example:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.pitest</groupId>
      <artifactId>pitest-maven</artifactId>
      <version>1.4.9</version>
      <configuration>
        <testPlugin>Cucumber</testPlugin>
    <targetClasses>
      <param>your.sut.package.*</param>
    </targetClasses>
    <targetTests>
      <param>your.test.package.*Test</param>
    </targetTests>
    <outputFormats>
          <outputFormat>XML</outputFormat>
          <outputFormat>HTML</outputFormat>
    </outputFormats>
      </configuration>
      <dependencies>
        <dependency>
      <groupId>com.github.alexvictoor</groupId>
      <artifactId>pitest-cucumber-plugin</artifactId>
      <version>1.0</version>
    </dependency>
      </dependencies>
    </plugin>
  </plugins>
</build>

```

Then as usual you just need to run pit using the following command:

    mvn org.pitest:pitest-maven:mutationCoverage

### Gradle

Just create `pitest` configuration and add this plugin as a dependency in a `buildscript` block in your root Gradle project. Below an example:
```
buildscript {
   repositories {
       mavenCentral()
   }
   configurations.maybeCreate("pitest")
   dependencies {
       classpath 'info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.4.9'
       pitest 'com.github.alexvictoor:pitest-cucumber-plugin:1.0'
   }
}
```

Then as usual you just need to run pit using the following command:

    gradle pitest

See [PIT plugin for Gradle README]( https://github.com/szpak/gradle-pitest-plugin) for more general configuration parameters.

Compatibility Matrix
--------------------

| Pitest Cucumber Plugin | tested against Cucumber | with Pitest Maven Plugin | notes |
|------------------------|-------------------------|--------------------------|-------|
| 0.1 | 1.1.8         | 1.1.5  | |
| 0.2 | 1.2.2         | 1.1.5  | |
| 0.3 | 1.2.2         | 1.1.5  | |
| 0.4 | 1.2.2         | 1.1.11 | |
| 0.5 | 1.2.2         | 1.4.2  | |
| 0.6 | 2.0.0 - 3.0.2 | 1.4.2  | |
| 0.7 | 4.0.0 - 4.2.0 | 1.4.3  | |
| 0.8 | 4.2.0         | 1.4.5  | |                |
| 0.9 | 4.5.4 - 4.7.1 | 1.4.9  | Using both deprecated and new annotations (io.cucumber.junit.Cucumber and io.cucumber.junit.CucumberOptions) |

Troubleshooting
-----------------
Before raising an issue on github or in the PIT's users mailing-list, please try to run the analysis in verbose mode. You just need to add a verbose flag in the configuration section of the maven plugin:

```xml
<configuration>
    ...
    <verbose>true</verbose>
</configuration>
```

If you are updating a project that was using pitest 1.1, be aware of the "testPlugin" config key in the maven configuration block:

```xml
<testPlugin>Cucumber</testPlugin> 
```

If you forget this one, the pitest cucumber plugin will not be used and no test will be run...
