// package com.jpmc.midascore;

// import com.jpmc.midascore.foundation.Transaction;
// import org.apache.kafka.clients.admin.AdminClientConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.core.*;
// import org.springframework.kafka.support.serializer.JsonDeserializer;
// import org.springframework.kafka.support.serializer.JsonSerializer;

// import java.util.HashMap;
// import java.util.Map;

// @Configuration
// public class KafkaConfig {

//     @Bean
//     public ProducerFactory<String, Transaction> producerFactory() {
//         Map<String, Object> props = new HashMap<>();
//         // DO NOT set bootstrap.servers here — let embedded Kafka inject it
//         props.put("key.serializer", StringSerializer.class);
//         props.put("value.serializer", JsonSerializer.class);
//         return new DefaultKafkaProducerFactory<>(props);
//     }

//     @Bean
//     public KafkaTemplate<String, Transaction> kafkaTemplate() {
//         return new KafkaTemplate<>(producerFactory());
//     }

//     @Bean
//     public ConsumerFactory<String, Transaction> consumerFactory() {
//         Map<String, Object> props = new HashMap<>();
//         // DO NOT set bootstrap.servers here
//         props.put("group.id", "midas-core");
//         props.put("auto.offset.reset", "earliest");

//         JsonDeserializer<Transaction> jsonDeserializer = new JsonDeserializer<>(Transaction.class);
//         jsonDeserializer.addTrustedPackages("*");
//         jsonDeserializer.setUseTypeHeaders(false);

//         return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
//     }

//     @Bean
//     public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListenerContainerFactory() {
//         ConcurrentKafkaListenerContainerFactory<String, Transaction> factory = new ConcurrentKafkaListenerContainerFactory<>();
//         factory.setConsumerFactory(consumerFactory());
//         return factory;
//     }
// }