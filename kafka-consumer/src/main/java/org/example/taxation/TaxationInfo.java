package org.example.taxation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TaxationInfo {

    private BigDecimal taxPercentage;

}
