package org.example.taxation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
class GrossSalaryCalculatorImpl implements GrossSalaryCalculator{


    @Override
    public BigDecimal calculate(BigDecimal netSalary, TaxationInfo taxationInfo) {
        if(netSalary.compareTo(BigDecimal.ZERO) < 0 ){
            throw new IllegalArgumentException("Cannot accept negative salaries");
        }

        log.info("Calculating Gross salary for net {} and taxationInfo {}", netSalary, taxationInfo);
        BigDecimal percentage = taxationInfo.getTaxPercentage();
        BigDecimal percentageAsFraction = percentage.divide(BigDecimal.valueOf(100));
        BigDecimal taxes = percentageAsFraction.multiply(netSalary);
        BigDecimal gross = netSalary.add(taxes);
        log.info("Gross salary = {}", gross.toPlainString());
        return gross;
    }
}
