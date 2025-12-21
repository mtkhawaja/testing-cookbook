package com.muneebkhawaja.testing.cookbook.bdd.feature1.steps;


import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;


@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.muneebkhawaja.testing.cookbook.bdd.feature1.steps")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.muneebkhawaja.testing.cookbook.bdd.feature1.steps"
)
@ConfigurationParameter(
        key = FEATURES_PROPERTY_NAME,
        value = "classpath:features/feature-one"
)
public class FeatureOneSuite {


}