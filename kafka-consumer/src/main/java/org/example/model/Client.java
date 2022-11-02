package org.example.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @With
    Long id;
    private String name;
    private String surname;
    BigDecimal wage;
    @Setter
    BigDecimal wageWithTax;
    @Setter
    BigDecimal taxPercentage;
    ZonedDateTime eventTime;


}
