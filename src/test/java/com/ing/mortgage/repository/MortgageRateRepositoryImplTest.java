package com.ing.mortgage.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ing.mortgage.model.MortgageRate;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MortgageRateRepositoryImplTest {

    @InjectMocks
    private MortgageRateRepositoryImpl repository;

    @Test
    void saveAndFindByMaturityPeriod_validInputs_shouldPersistAndFetch() {
        MortgageRate rate = new MortgageRate(15, 2.5, Instant.now());

        repository.save(rate);

        Optional<MortgageRate> found = repository.findByMaturityPeriod(15);
        assertTrue(found.isPresent());
        assertEquals(2.5, found.get().interestRate());
    }

    @Test
    void findByMaturityPeriod_ifNotFound_shouldReturnEmpty() {
        assertTrue(repository.findByMaturityPeriod(99).isEmpty());
    }

    @Test
    void findAll_withData_shouldReturnSortedList() {
        MortgageRate rate10 = new MortgageRate(10, 2.0, Instant.now());
        MortgageRate rate30 = new MortgageRate(30, 3.0, Instant.now());

        repository.save(rate30);
        repository.save(rate10);

        List<MortgageRate> all = repository.findAll();
        assertEquals(2, all.size());
        assertEquals(10, all.get(0).maturityPeriod());
        assertEquals(30, all.get(1).maturityPeriod());
    }

    @Test
    void save_existingRate_shouldUpdate() {
        MortgageRate rate = new MortgageRate(20, 2.8, Instant.now());
        repository.save(rate);

        MortgageRate newRate = new MortgageRate(20, 3.1, Instant.now());
        repository.save(newRate);

        Optional<MortgageRate> found = repository.findByMaturityPeriod(20);
        assertTrue(found.isPresent());
        assertEquals(3.1, found.get().interestRate());
    }
}