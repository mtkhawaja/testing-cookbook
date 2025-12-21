package com.muneebkhawaja.testing.cookbook.bdd.feature1.steps;

import io.cucumber.spring.ScenarioScope;
import lombok.Data;
import org.springframework.boot.test.context.TestComponent;

@Data
@TestComponent
@ScenarioScope
public class FeatureOneState {
    private String inputText;
    private String outputText;
}
