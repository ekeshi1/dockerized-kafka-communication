package org.example.dto;

import org.example.model.Client;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClientDtoMapper {

    public Client mapToDomain(ClientDTO clientDTO){

        Client client = Client.builder()
                .name(clientDTO.getName())
                .surname(clientDTO.getSurname())
                .wage(clientDTO.getWage())
                .eventTime(clientDTO.getEventTime())
                .build();

        return client;
    }
}
