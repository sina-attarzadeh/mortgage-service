package com.ing.mortgage.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MortgageRequest {

    @NotNull
    @Min(1)
    private Integer maturityPeriod;

    @NotNull
    @Min(0)
    private BigDecimal income;

    @NotNull
    @Min(1)
    private BigDecimal loanValue;

    @NotNull
    @Min(1)
    private BigDecimal homeValue;

}
