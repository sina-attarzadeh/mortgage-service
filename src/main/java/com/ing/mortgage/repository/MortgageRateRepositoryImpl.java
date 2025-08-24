package com.ing.mortgage.repository;

import com.ing.mortgage.model.MortgageRate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of the MortgageRateRepository interface. This implementation uses a ConcurrentHashMap to
 * store mortgage rates, allowing for thread-safe operations.
 */
@Repository
public class MortgageRateRepositoryImpl implements MortgageRateRepository {

    private final Map<Integer, MortgageRate> mortgageRates = new ConcurrentHashMap<>();

    @Override
    public void save(MortgageRate mortgageRate) {
        mortgageRates.put(mortgageRate.maturityPeriod(), mortgageRate);
    }

    @Override
    public List<MortgageRate> findAll() {
        return mortgageRates.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).toList();
    }

    @Override
    public Optional<MortgageRate> findByMaturityPeriod(int maturityPeriod) {
        return mortgageRates.containsKey(maturityPeriod) ? Optional.of(mortgageRates.get(maturityPeriod))
            : Optional.empty();
    }
}
