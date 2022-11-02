package org.example.taxation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GrossSalaryCalculatorImplTest {

    GrossSalaryCalculator grossSalaryCalculator = new GrossSalaryCalculatorImpl();

    private static Stream<Arguments> taxTestSet() {
        return Stream.of(
                arguments(BigDecimal.valueOf(10),BigDecimal.valueOf(1000),BigDecimal.valueOf(1100)),
                arguments(BigDecimal.valueOf(0), BigDecimal.valueOf(1000), BigDecimal.valueOf(1000)),
                arguments(BigDecimal.valueOf(1.02),BigDecimal.valueOf(1), BigDecimal.valueOf(1.0102))
        );
    }

    @ParameterizedTest
    @MethodSource("taxTestSet")
    void testCalculateWorksCorrectly(BigDecimal taxPercentage, BigDecimal netSalary, BigDecimal grossSalary) {
        TaxationInfo taxationInfo = new TaxationInfo(taxPercentage);
        BigDecimal calculatedGross  = grossSalaryCalculator.calculate(netSalary, taxationInfo);
        assertThat(grossSalary, Matchers.comparesEqualTo(calculatedGross));
    }

    @Test
    void testNegativeSalaryThrowsIllegalArgExc(){
        TaxationInfo taxationInfo = new TaxationInfo(BigDecimal.ONE);
        Assertions.assertThrows(IllegalArgumentException.class, ()
                -> grossSalaryCalculator.calculate(BigDecimal.valueOf(-1), taxationInfo));
    }



}