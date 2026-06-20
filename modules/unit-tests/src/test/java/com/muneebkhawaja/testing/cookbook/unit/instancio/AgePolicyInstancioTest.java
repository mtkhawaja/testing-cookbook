package com.muneebkhawaja.testing.cookbook.unit.instancio;

import java.util.List;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/// Demonstrates Instancio for auto-generating test data:
/// - `Instancio.create(...)` fully populates an object graph
/// - `set(...)` / `generate(...)` with `field(...)` selectors pin only the fields under test
/// - `ofList(...)` builds collections
/// - `withSeed(...)` makes generated data reproducible
class AgePolicyInstancioTest {

    private final AgePolicy agePolicy = new AgePolicy();

    @DisplayName("Should fully populate every field When a customer is created")
    @Test
    void shouldFullyPopulateEveryFieldWhenACustomerIsCreated() {
        final Customer customer = Instancio.create(Customer.class);

        assertThat(customer.id()).isNotBlank();
        assertThat(customer.firstName()).isNotBlank();
        assertThat(customer.lastName()).isNotBlank();
        assertThat(customer.email()).isNotBlank();
    }

    @DisplayName("Should treat the customer as a minor When only the age field is set below the threshold")
    @Test
    void shouldTreatTheCustomerAsAMinorWhenOnlyTheAgeFieldIsSetBelowTheThreshold() {
        final Customer customer = Instancio.of(Customer.class)
                .set(field(Customer::age), 17)
                .create();

        assertThat(agePolicy.isAdult(customer)).isFalse();
    }

    @DisplayName("Should treat the customer as an adult When the generated age is within the adult range")
    @Test
    void shouldTreatTheCustomerAsAnAdultWhenTheGeneratedAgeIsWithinTheAdultRange() {
        final Customer customer = Instancio.of(Customer.class)
                .generate(field(Customer::age), gen -> gen.ints().range(AgePolicy.MINIMUM_ADULT_AGE, 65))
                .create();

        assertThat(agePolicy.isAdult(customer)).isTrue();
    }

    @DisplayName("Should generate the requested number of customers When building a list")
    @Test
    void shouldGenerateTheRequestedNumberOfCustomersWhenBuildingAList() {
        final List<Customer> customers = Instancio.ofList(Customer.class).size(5).create();

        assertThat(customers).hasSize(5).allSatisfy(customer -> assertThat(customer.email()).isNotBlank());
    }

    @DisplayName("Should produce identical data When the same seed is used")
    @Test
    void shouldProduceIdenticalDataWhenTheSameSeedIsUsed() {
        final Customer first = Instancio.of(Customer.class).withSeed(42L).create();
        final Customer second = Instancio.of(Customer.class).withSeed(42L).create();

        assertThat(first).isEqualTo(second);
    }
}
