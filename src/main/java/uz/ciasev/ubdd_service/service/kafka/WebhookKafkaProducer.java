package uz.ciasev.ubdd_service.service.kafka;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static uz.ciasev.ubdd_service.config.kafka.KafkaTopicConfig.PUBLIC_API_WEBHOOK;

@Service
public class WebhookKafkaProducer {

    private final KafkaTemplate<String, WebhookEventMessage> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(WebhookKafkaProducer.class);


    public WebhookKafkaProducer(KafkaTemplate<String, WebhookEventMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(WebhookEventMessage message) {
        try {
            kafkaTemplate.send(PUBLIC_API_WEBHOOK, message).addCallback(
                    result -> logger.info("Message sent successfully to topic: {}", PUBLIC_API_WEBHOOK),
                    ex -> logger.error("Error sending message to topic: {}", PUBLIC_API_WEBHOOK, ex)
            );
        } catch (Exception e) {
            logger.error("Error sending message to topic {}", PUBLIC_API_WEBHOOK,e);
        }
    }
}
