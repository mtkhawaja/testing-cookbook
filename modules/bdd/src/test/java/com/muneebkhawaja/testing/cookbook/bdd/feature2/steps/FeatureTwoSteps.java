package com.muneebkhawaja.testing.cookbook.bdd.feature2.steps;

import com.muneebkhawaja.testing.cookbook.bdd.feature2.FeatureTwo;
import com.muneebkhawaja.testing.cookbook.bdd.feature2.ValueEntry;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FeatureTwoSteps {
    private final FeatureTwo featureTwo;
    private List<ValueEntry> entries;
    private long result;

    public FeatureTwoSteps(FeatureTwo featureTwo) {
        this.featureTwo = featureTwo;
    }

    @Given("the following values:")
    public void theFollowingValues(final List<ValueEntry> entries) {
        this.entries = entries;
    }

    @When("feature two sums the values")
    public void featureTwoSumsTheValues() {
        this.result = this.featureTwo.sumEntries(this.entries);
    }

    @Then("the result should be {int}")
    public void theResultShouldBe(int expectedSum) {
        assertThat(this.result).isEqualTo(expectedSum);
    }

    @DataTableType
    public ValueEntry valueEntry(Map<String, String> entry) {
        return new ValueEntry(
                Objects.requireNonNull(entry.get("label"), "Label cannot be null"),
                Integer.parseInt(Objects.requireNonNull(entry.get("value"), "Value cannot be null"))
        );
    }


}
