package com.ing.mortgage.bootstrap;

import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.repository.MortgageRateRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * Service to bootstrap initial mortgage rates into the database. This service runs on application startup.
 */
@Service
@RequiredArgsConstructor
public class BootstrapService implements ApplicationRunner {

    private final MortgageRateRepository mortgageRateRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mortgageRateRepository.save(new MortgageRate(10, 2.5, Instant.now()));
        mortgageRateRepository.save(new MortgageRate(15, 3.0, Instant.now()));
        mortgageRateRepository.save(new MortgageRate(20, 4.1, Instant.now()));
        mortgageRateRepository.save(new MortgageRate(30, 5.0, Instant.now()));
    }
}
