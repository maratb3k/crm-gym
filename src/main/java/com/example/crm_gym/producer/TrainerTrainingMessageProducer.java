package com.example.crm_gym.producer;

import com.example.crm_gym.dto.TrainingSessionRequestDTO;
import com.example.crm_gym.security.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TrainerTrainingMessageProducer {

    private final JmsTemplate jmsTemplate;

    public TrainerTrainingMessageProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendTrainingSessionMessage(String queueName, String jsonRequest,
                                           String transactionId, String token) {
        jmsTemplate.convertAndSend("training-session-queue", jsonRequest, message -> {
            message.setStringProperty("transactionId", transactionId);
            message.setStringProperty("Authorization", "Bearer " + JwtAuthenticationFilter.getCurrentToken());
            return message;
        });
        log.info("Message sent to queue {}: {}", queueName, jsonRequest);
    }

}
