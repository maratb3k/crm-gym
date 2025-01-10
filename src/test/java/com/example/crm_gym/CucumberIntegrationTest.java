package com.example.crm_gym;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features/controller/",
        glue = {"com.example.crm_gym.steps"})
public class CucumberIntegrationTest {

}