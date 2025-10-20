package com.example.file_service.config;

import com.example.dto.CreateVideoDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;
    @Value("${kafka.group-id}")
    private String groupId;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    @Bean
    public ProducerFactory<String, CreateVideoDTO> producerFactoryCreateVideoDTO() {
        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, CreateVideoDTO> kafkaTemplateCreateVideoDTO() {
        return new KafkaTemplate<>(producerFactoryCreateVideoDTO());
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactoryString() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> concurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryString());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CreateVideoDTO> consumerFactoryCreateVideoDTO() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateVideoDTO>
                    concurrentKafkaListenerContainerFactoryCreateVideoDTO() {
        ConcurrentKafkaListenerContainerFactory<String, CreateVideoDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryCreateVideoDTO());
        return factory;
    }

    private Map<String, Object> getConsumerConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }

    private Map<String, Object> getProducerConfig() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
