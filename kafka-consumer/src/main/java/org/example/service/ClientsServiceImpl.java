package org.example.service;


import lombok.extern.slf4j.Slf4j;
import org.example.config.TaxationProperties;
import org.example.dao.ClientsRepository;
import org.example.dto.ClientDtoMapper;
import org.example.model.Client;
import org.example.taxation.GrossSalaryCalculator;
import org.example.taxation.TaxationInfo;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class ClientsServiceImpl implements ClientsService{

    ClientsRepository clientsRepository;

    GrossSalaryCalculator grossSalaryCalculator;

    TaxationProperties taxationProperties;

    public ClientsServiceImpl(ClientsRepository clientsRepository, GrossSalaryCalculator grossSalaryCalculator, TaxationProperties taxationProperties) {
        this.clientsRepository = clientsRepository;
        this.grossSalaryCalculator = grossSalaryCalculator;
        this.taxationProperties = taxationProperties;
    }

    @Override
    public Client processClient(Client client)  {
        log.info("Processing client request " + client);
        TaxationInfo taxationInfo = taxationProperties.toDomain();
        BigDecimal gross = grossSalaryCalculator.calculate(client.getWage(),  taxationInfo);

        client.setTaxPercentage(taxationInfo.getTaxPercentage());
        client.setWageWithTax(gross);

        Client savedClient;

        try {
            savedClient = clientsRepository.save(client);
            log.info("Stored client id={}", savedClient.getId());
        } catch (RuntimeException e){
            throw new RecoverableDataAccessException("Error while saving the client",e);
        }

        return savedClient;
    }


}
