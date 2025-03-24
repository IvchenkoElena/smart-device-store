package ru.yandex.practicum.config;

import deserializer.SensorsSnapshotDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.snapshot-consumer-properties.group-id}")
    private String snapshotGroupId;

    @Value("${kafka.hub-consumer-properties.group-id}")
    private String hubGroupId;

    @Value("${kafka.key-deserializer}")
    private String keyDeserializer;

//    @Value("${kafka.snapshot-consumer-properties.value-deserializer}")
//    private String snapshotValueDeserializer;

    @Value("${kafka.hub-consumer-properties.value-deserializer}")
    private String hubValueDeserializer;

    @Bean
    public KafkaConsumer<String, SensorsSnapshotAvro> snapshotsConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<String, HubEventAvro> hubsConsumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubValueDeserializer);
        return new KafkaConsumer<>(properties);
    }

}
