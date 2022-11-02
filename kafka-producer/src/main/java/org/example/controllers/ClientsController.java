package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.ClientProcessor;
import org.example.ProcessingResult;
import org.example.dto.ClientDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.example.ProcessingResultStatus.OK;

@RestController
@RequestMapping("/clients")
@Slf4j
public class ClientsController {
    private ClientProcessor clientProcessor;

    public ClientsController(ClientProcessor clientProcessor) {
        this.clientProcessor = clientProcessor;
    }

    @PostMapping
    public ResponseEntity createClient(@Validated @RequestBody ClientDTO clientDTO){

        ProcessingResult processingResult = clientProcessor.process(clientDTO);
        if(OK.equals(processingResult.getStatus())){
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.internalServerError().body(processingResult);

    }


}
