package com.example.crm_gym.producer;

import jakarta.jms.MapMessage;
import jakarta.jms.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class TrainerDurationMessageListener {

    private final ResponseRegistry responseRegistry;

    @Autowired
    public TrainerDurationMessageListener(ResponseRegistry responseRegistry) {
        this.responseRegistry = responseRegistry;
    }

    @JmsListener(destination = "trainer-duration-response-queue")
    public void processTrainerDurationResponse(Message message) {
        try {
            if (message instanceof MapMessage mapMessage) {
                double duration = mapMessage.getDouble("duration");
                String transactionId = mapMessage.getString("transactionId");

                log.info("Received response: duration={}, transactionId={}", duration, transactionId);
                CompletableFuture<Double> futureResponse = responseRegistry.getFutureForTransaction(transactionId);
                if (futureResponse != null) {
                    futureResponse.complete(duration);
                } else {
                    log.warn("No matching request found for transactionId: {}", transactionId);
                }

            } else {
                log.error("Unsupported message type received in main service: {}", message.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error processing trainer duration response: {}", e.getMessage(), e);
        }
    }

}
