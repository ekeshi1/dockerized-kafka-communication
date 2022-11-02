package org.example;

import org.example.dto.ClientDTO;

public interface ClientProcessor {

    ProcessingResult process(ClientDTO clientDTO);

    void evacuateUnprocessed(ClientDTO clientDTO);
}
