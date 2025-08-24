package com.ing.mortgage.model;

import java.time.Instant;

public record MortgageRate(Integer maturityPeriod, Double interestRate, Instant lastUpdated) {

}