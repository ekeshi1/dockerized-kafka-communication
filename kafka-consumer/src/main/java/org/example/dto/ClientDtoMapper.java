package org.example.dto;

import org.example.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoMapper {

    public Client mapToDomain(ClientDTO clientDTO){

        return Client.builder()
                .name(clientDTO.getName())
                .surname(clientDTO.getSurname())
                .wage(clientDTO.getWage())
                .eventTime(clientDTO.getEventTime())
                .build();
    }
}
