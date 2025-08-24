package com.ing.mortgage.repository;

import com.ing.mortgage.model.MortgageRate;
import java.util.List;
import java.util.Optional;

public interface MortgageRateRepository {

    void save(MortgageRate mortgageRate);

    List<MortgageRate> findAll();

    Optional<MortgageRate> findByMaturityPeriod(int maturityPeriod);
}
