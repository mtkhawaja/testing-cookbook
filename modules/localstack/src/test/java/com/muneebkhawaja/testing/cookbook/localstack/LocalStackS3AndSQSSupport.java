package com.muneebkhawaja.testing.cookbook.localstack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

///
///
///  Add support for SQS/S3
/// ```java
/// @SpringBootTest
/// @Import(LocalStackS3AndSQSSupport.class)
/// class MyTest {
///
/// }
/// ```
///
/// Make sure to using Spring Cloud AWS 4.x or above.
///
/// 1. {@code org.testcontainers.localstack.LocalStackContainer} (latest)
/// 2. {@code org.testcontainers.containers.localstack.LocalStackContainer} - Deprecated
///
/// Also see:
/// - [gh-793 Add @ServiceConnection support](https://github.com/awspring/spring-cloud-aws/pull/1075) for details on
/// LocalStack Service Connection Support
/// - See [discussion](https://github.com/awspring/spring-cloud-aws/discussions/1500)
/// - [Spring Cloud AWS Docs](https://docs.awspring.io/spring-cloud-aws/docs/4.0.0-M1/reference/html/index.html)
/// - [Spring Test Containers Support](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html)
/// - [LocalStack Module](https://java.testcontainers.org/modules/localstack/)
/// - [Service Connections](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html#testing.testcontainers.service-connections)

@TestConfiguration(proxyBeanMethods = false)
public final class LocalStackS3AndSQSSupport {
    private final String bucketName;
    private final String queueName;

    public LocalStackS3AndSQSSupport(@Value("${object.repository.s3.bucket-name:example-bucket}") String bucketName,
                                     @Value("${notifications.object-repository.events.queue}") String queueName) {
        this.bucketName = bucketName;
        this.queueName = queueName;
    }

    @Bean
    @ServiceConnection
    LocalStackContainer localstackContainer() throws Exception {
        final var localStackImage = DockerImageName.parse("localstack/localstack:4.12.0");
        final var localStack = new LocalStackContainer(localStackImage);
        localStack.start();
        localStack.execInContainer("awslocal", "s3", "mb", "s3://" + this.bucketName);
        localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", this.queueName);
        return localStack;
    }


}