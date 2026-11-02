package org.example.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "org.example",
        tags = "@test",
        plugin = {
                "pretty",
                "html:target/cucumber/cucumber-report.html",
                "json:target/cucumber/cucumber.json"
        },
        stepNotifications = true
)
public class RunnerTest {
}