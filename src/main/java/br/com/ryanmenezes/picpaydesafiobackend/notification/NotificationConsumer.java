package br.com.ryanmenezes.picpaydesafiobackend.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import br.com.ryanmenezes.picpaydesafiobackend.transaction.Transaction;

@Service
public class NotificationConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConsumer.class);

    private final RestClient restClient;

    public NotificationConsumer(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("https://util.devi.tools/api/v1/notify")
            .build();
    }

    @KafkaListener(topics = "transaction-notification", groupId = "picpay-desafio-backend")
    public void receiveNotification(Transaction transaction) {
        LOGGER.info("Notifying transaction: {}", transaction);

        var response = restClient
            .post()
            .retrieve()
            .toEntity(Notification.class);

        if (response.getStatusCode().isError()) {
            throw new NotificationException("Error sending notification!");
        }

        LOGGER.info("Notification has benn sent: {}...", transaction);
    }
}
