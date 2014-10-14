Cucumber PIT integration plugin
========================

Out of the box [mutation testing tool PIT](http://pitest.org) does not take in account cucumber features and scenarios to kill mutants in your code.  
The purpose of this plugin is to make PIT use your cucumber scenarios the same way it use already your regular junit tests.

Limitations
------------
Today, PIT plugins require maven. You cannot for the moment use this plugin with the PIT command line interface or the [graddle plugin](https://github.com/szpak/gradle-pitest-plugin/issues/17).
Today PIT 1.0.1 has not been released and hence you need to use a snapshot version of PIT.

Usage
------
Just add this plugin as a maven dependency of PIT maven plugin. Below an example:

```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.pitest</groupId>
			<artifactId>pitest-maven</artifactId>
			<version>1.0.1-SNAPSHOT</version>
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
					<groupId>org.pitest</groupId>
					<artifactId>pitest-cucumber-plugin</artifactId>
					<version>1.0-SNAPSHOT</version>
				</dependency>
        	</dependencies>
		</plugin>
	</plugins>
</build>

```
