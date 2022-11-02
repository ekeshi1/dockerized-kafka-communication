package org.example.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ClientProcessor;
import org.example.ProcessingResult;
import org.example.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import static org.example.ProcessingResultStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaClientProcessor implements ClientProcessor {

    @Value("${topic.name.producer}")
    private String topicName;
    KafkaTemplate<String, ClientDTO> kafkaTemplate;

    @Autowired
    public KafkaClientProcessor(KafkaTemplate<String, ClientDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProcessingResult process(ClientDTO clientDTO) {

        ListenableFuture<SendResult<String, ClientDTO>> future = kafkaTemplate.send(topicName, clientDTO);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to deliver message {}. {}", clientDTO, ex.getMessage());
                evacuateUnprocessed(clientDTO);
            }

            @Override
            public void onSuccess(SendResult<String, ClientDTO> result) {
                log.info("Message [{}] delivered with offset {} to topic {}", clientDTO, result.getRecordMetadata().offset(), topicName);
            }
        });

        return new ProcessingResult(OK);
    }

    @Override
    public void evacuateUnprocessed(ClientDTO clientDTO) {
        // TODO: do something here, maybe store to db ?
    }
}
