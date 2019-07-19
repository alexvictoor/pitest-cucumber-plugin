package cucumber.examples.java.calculator;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.Ignore;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:cucumber/examples/java/calculator/date_calculator.feature")
@Ignore
public class Cornichon {
}
