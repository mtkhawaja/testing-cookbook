package com.muneebkhawaja.testing.cookbook.testng;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// See [Spring TestNG Support](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/support-classes.html#testcontext-support-classes-testng) for additional information
///
@ContextConfiguration(classes = {TestApplication.class})
@Test(groups = {"integration"})
public class StringStatisticsIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StringStatisticsService service;
    @Autowired
    private StringStatisticsReporter reporter;

    @Test
    void shouldGenerateReportWhenGivenValidStringStatistics() {
        final StringStatistics stats = service.calculate("Mississippi");
        final String report = reporter.generateReport(stats);
        assertThat(report).isNotBlank();
    }


}