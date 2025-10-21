package com.example.business.config;

import com.example.business.dto.CompensatingTransactionDTO;
import com.example.business.dto.KafkaDeleteChannelDTO;
import com.example.dto.UserDTO;
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
    public ProducerFactory<String, UserDTO> producerFactoryUserDTO() {
        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, UserDTO> kafkaTemplateUserDTO() {
        return new KafkaTemplate<>(producerFactoryUserDTO());
    }

    @Bean
    public ConsumerFactory<String, UserDTO> consumerFactoryUserDTO() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>(UserDTO.class));
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactoryString() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>());
    }

    @Bean
    public ConsumerFactory<String, KafkaDeleteChannelDTO> consumerFactoryKafkaDeleteChannelDTO() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>(KafkaDeleteChannelDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDTO> kafkaTemplateTaskDetailsListener() {
        ConcurrentKafkaListenerContainerFactory<String, UserDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryUserDTO());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaDeleteChannelDTO> factoryKafkaDeleteChannelDTO() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaDeleteChannelDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryKafkaDeleteChannelDTO());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CompensatingTransactionDTO> compensatingTransactionDTOConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfig(), new StringDeserializer(),
                new JsonDeserializer<>(CompensatingTransactionDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CompensatingTransactionDTO> factoryCompensatingTransactionDTO() {
        ConcurrentKafkaListenerContainerFactory<String, CompensatingTransactionDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(compensatingTransactionDTOConsumerFactory());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> concurrentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryString());
        return factory;
    }

    private Map<String, Object> getConsumerConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000,
                ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000,
                ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 5000
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
