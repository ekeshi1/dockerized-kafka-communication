package org.example.taxation;

import java.math.BigDecimal;

public interface GrossSalaryCalculator {

     BigDecimal calculate(BigDecimal netSalary, TaxationInfo taxationInfo);
}
