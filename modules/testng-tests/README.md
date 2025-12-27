# TestNG + Spring

```shell
#!/usr/bin/env bash

# Note: -Dtestng.suite is an arbitrary property setup in the POM; It could've been anything
# Run Unit Tests Group
mvn test -Dtestng.suite=src/test/resources/suites/unit-tests.xml -pl modules/testng-tests

# Run Integration Tests Group
mvn test -Dtestng.suite=src/test/resources/suites/integration-tests.xml -pl modules/testng-tests

# Run Everything (Default)
mvn test -Dtestng.suite=src/test/resources/suites/all.xml -pl modules/testng-tests
```

## References

- [TestNG Docs](https://testng.org/)
- [TestContext Framework Support Classes > TestNG Support](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/support-classes.html#testcontext-support-classes-testng)
- [Surefire + TestNG](https://maven.apache.org/surefire/maven-surefire-plugin/examples/testng.html)