package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.taxation.TaxationInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Configuration
@PropertySource("classpath:taxation/taxes.properties")
@Slf4j
public class TaxationProperties {


    BigDecimal taxPercentage;

    public TaxationProperties(@Value( "${taxPercentage}" )BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public TaxationInfo toDomain(){
        return new TaxationInfo(taxPercentage);
    }


    @PostConstruct
    public void checkPost(){
        log.info(String.valueOf(taxPercentage));
    }

}
