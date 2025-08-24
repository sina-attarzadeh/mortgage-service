package com.ing.mortgage.service;

import com.ing.mortgage.domain.MortgageCheckResponse;
import com.ing.mortgage.domain.MortgageRequest;
import com.ing.mortgage.model.MortgageRate;
import java.util.List;

/**
 * Find mortgage rates and check mortgage feasibility. Possible implementations include annuity and linear mortgages.
 */
public interface MortgageService {

    List<MortgageRate> getInterestRates();

    MortgageCheckResponse mortgageCheck(MortgageRequest mortgageRequest);
}
