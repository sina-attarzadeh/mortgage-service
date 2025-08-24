package com.ing.mortgage.domain;

import java.math.BigDecimal;

public record MortgageCheckResponse(boolean feasible, BigDecimal monthlyPayment, String message) {

}
