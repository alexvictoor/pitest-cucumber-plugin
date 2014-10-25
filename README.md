[![Build Status](https://travis-ci.org/alexvictoor/pitest-cucumber-plugin.svg?branch=master)](https://travis-ci.org/alexvictoor/pitest-cucumber-plugin)

Cucumber PIT integration plugin
========================

Out of the box [mutation testing tool PIT](http://pitest.org) does not take in account cucumber features and scenarios to kill mutants in your code.  
The purpose of this plugin is to make PIT use your cucumber scenarios the same way it use already your regular junit tests.

Limitations
------------
Today, PIT plugins require maven. You cannot for the moment use this plugin with the PIT command line interface or the [graddle plugin](https://github.com/szpak/gradle-pitest-plugin/issues/17).

Usage
------
Just add this plugin as a maven dependency of PIT maven plugin. Below an example:

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.pitest</groupId>
			<artifactId>pitest-maven</artifactId>
			<version>1.1.0</version>
			<configuration>
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
					<version>0.1</version>
				</dependency>
        	</dependencies>
		</plugin>
	</plugins>
</build>

```

Then as usual you just need to run pit using the following command:

    mvn org.pitest:pitest-maven:mutationCoverage

Troubleshooting
-----------------
Before raising an issue on github or in the PIT's users mailing-list, please try to run the analysis in verbose mode. You just need to add a verbise flag in the configuration section of the maven plugin:

```xml
<configuration>
	...
	<verbose>true</verbose>
</configuration>
```

