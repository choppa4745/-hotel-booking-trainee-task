package edu.booking.hotel_booking.config;

import edu.booking.hotel_booking.kafka.KafkaTopicsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaTopicsProperties.class)
public class KafkaConfig {
}
