package org.example.kafka;


import lombok.extern.slf4j.Slf4j;
import org.example.dto.ClientDTO;
import org.example.dto.ClientDtoMapper;
import org.example.model.Client;
import org.example.service.ClientsService;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaReliableListener {

    ClientsService clientsService;
    ClientDtoMapper clientDtoMapper;

    public KafkaReliableListener(ClientsService clientsService, ClientDtoMapper clientDtoMapper) {
        this.clientsService = clientsService;
        this.clientDtoMapper = clientDtoMapper;
    }

    /**
     *
     * Consumer's only external connection is with the database. All other exceptions would require
     * some sort of intervention. Hence only TransientDataAccessExceptions will be retried. The rest will be sent
     * to dead letter topic.
     */

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            autoCreateTopics = "false",
            include = {RecoverableDataAccessException.class})
    @org.springframework.kafka.annotation.KafkaListener(topics = "client-topic")
    public void listenNewClient(@Payload ClientDTO message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)  {
        log.info("Received message [{}]in topic {}", message, topic);
        Client client = clientDtoMapper.mapToDomain(message);
        clientsService.processClient(client);
     }


     @DltHandler
     public void handleDeadLetterTopic(@Payload ClientDTO in, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        log.info(in + " from " + topic);
     }
}
