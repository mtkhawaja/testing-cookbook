package com.muneebkhawaja.testing.cookbook.bdd.feature1.steps;

import com.muneebkhawaja.testing.cookbook.bdd.feature1.FeatureOne;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FeatureOneSteps {
    private final FeatureOne featureOne;
    private final FeatureOneState state;

    public FeatureOneSteps(FeatureOne featureOne, FeatureOneState state) {
        this.featureOne = featureOne;
        this.state = state;
    }

    @Given("a text {string}")
    public void a_text(String string) {
        this.state.setInputText(string);
    }

    @When("action one is executed")
    public void action_one_is_executed() {
        this.state.setOutputText(this.featureOne.actionOne(this.state.getInputText()));
    }

    @When("action two is executed")
    public void action_two_is_executed() {
        this.state.setOutputText(this.featureOne.actionTwo(this.state.getInputText()));
    }

    @When("action three is executed")
    public void action_three_is_executed() {
        this.state.setOutputText(this.featureOne.actionThree(this.state.getInputText()));
    }


    @Then("the result should be {string}")
    public void the_result_should_be(String string) {
        assertThat(this.state.getOutputText())
                .isEqualTo(string);
    }

}
