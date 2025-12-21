package com.muneebkhawaja.testing.cookbook.bdd.feature1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeatureOne {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOne.class);

    public String actionOne(String text) {
        LOGGER.info("Action One: {}", text);
        return text;
    }

    public String actionTwo(String text) {
        LOGGER.info("Action Two: {}", text);
        return text;
    }

    public String actionThree(String text) {
        LOGGER.info("Action Three: {}", text);
        return text;
    }
}
