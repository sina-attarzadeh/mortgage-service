package com.ing.mortgage.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.repository.MortgageRateRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BootstrapServiceIT {

    @Autowired
    private MortgageRateRepository mortgageRateRepository;

    @Test
    void run_withMortgageRates_shouldIngest() {
        List<MortgageRate> rates = mortgageRateRepository.findAll();

        assertThat(rates)
            .extracting(MortgageRate::maturityPeriod, MortgageRate::interestRate)
            .containsExactlyInAnyOrder(
                org.assertj.core.groups.Tuple.tuple(10, 2.5),
                org.assertj.core.groups.Tuple.tuple(15, 3.0),
                org.assertj.core.groups.Tuple.tuple(20, 4.1),
                org.assertj.core.groups.Tuple.tuple(30, 5.0)
            );
    }
}