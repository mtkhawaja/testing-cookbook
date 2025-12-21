package com.muneebkhawaja.testing.cookbook.bdd.feature2.steps;

import com.muneebkhawaja.testing.cookbook.bdd.TestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

///
/// > The class annotated with @CucumberContextConfiguration is instantiated but not initialized by Spring.
/// > Instead, this instance is processed by Springs test execution listeners.
/// > So Spring features that depend on a test execution listeners, such as mock beans, will work on the annotated class
/// > but not on other step definition classes. - [Configuring the Test Application Context](https://github.com/cucumber/cucumber-jvm/tree/main/cucumber-spring#configuring-the-test-application-context)
@CucumberContextConfiguration
@SpringBootTest(classes = TestApplication.class)
class FeatureTwoCucumberSpringConfiguration {
}
