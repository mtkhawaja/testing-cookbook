package com.muneebkhawaja.testing.cookbook.testng;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

///
/// Straight forward test taken right out of the [TestNG welcome documentation](https://testng.org/#_welcome_to_testng).
/// Nothing special here.
///
@Test(groups = {"unit"})
public class SimpleTest {

    @BeforeClass
    public void setUp() {
        // code that will be invoked when this test is instantiated
    }

    @Test(groups = {"fast"})
    public void aFastTest() {
        System.out.println("Fast test");
    }

    @Test(groups = {"slow"})
    public void aSlowTest() {
        System.out.println("Slow test");
    }

}