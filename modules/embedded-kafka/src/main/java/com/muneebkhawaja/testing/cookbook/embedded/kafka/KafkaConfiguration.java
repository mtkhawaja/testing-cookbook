package com.muneebkhawaja.testing.cookbook.embedded.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class KafkaConfiguration {
    private final String bootstrapServers;
    private final String quackTopic;

    public KafkaConfiguration(
            @Value(value = "${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${app.kafka.topic.quack:quacks}") String quackTopic
    ) {
        this.bootstrapServers = bootstrapServers;
        this.quackTopic = quackTopic;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(this.quackTopic)
                .build();
    }

    @Bean
    public ProducerFactory<UUID, QuackEvent> producerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<UUID, QuackEvent> kafkaTemplate(final ProducerFactory<UUID, QuackEvent> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public ConsumerFactory<UUID, QuackEvent> consumerFactory() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dummy-group");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.muneebkhawaja.testing.cookbook.embedded.kafka.*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, QuackEvent.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final var deserializer = new JsonDeserializer<>(QuackEvent.class, false);
        return new DefaultKafkaConsumerFactory<>(props, new UUIDDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<UUID, QuackEvent> kafkaListenerContainerFactory(
            ConsumerFactory<UUID, QuackEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<UUID, QuackEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

}
