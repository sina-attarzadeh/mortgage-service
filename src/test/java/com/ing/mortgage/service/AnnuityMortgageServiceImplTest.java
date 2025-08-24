package com.ing.mortgage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ing.mortgage.domain.MortgageCheckResponse;
import com.ing.mortgage.domain.MortgageRequest;
import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.repository.MortgageRateRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnnuityMortgageServiceImplTest {

    @InjectMocks
    private AnnuityMortgageServiceImpl mortgageService;

    @Mock
    private MortgageRateRepository mortgageRateRepository;

    record FeasibleCase(MortgageRequest request, BigDecimal expectedMonthlyPayment) {

    }

    static Stream<FeasibleCase> feasibleRequests() {
        MortgageRequest req1 = new MortgageRequest();
        req1.setMaturityPeriod(20);
        req1.setIncome(BigDecimal.valueOf(50000));
        req1.setLoanValue(BigDecimal.valueOf(100000));
        req1.setHomeValue(BigDecimal.valueOf(150000));

        MortgageRequest req2 = new MortgageRequest();
        req2.setMaturityPeriod(15);
        req2.setIncome(BigDecimal.valueOf(60000));
        req2.setLoanValue(BigDecimal.valueOf(120000));
        req2.setHomeValue(BigDecimal.valueOf(200000));

        BigDecimal expected1 = BigDecimal.valueOf(579.96);
        BigDecimal expected2 = BigDecimal.valueOf(857.86);

        return Stream.of(
            new FeasibleCase(req1, expected1),
            new FeasibleCase(req2, expected2)
        );
    }

    @ParameterizedTest
    @MethodSource("feasibleRequests")
    void mortgageCheck_feasibleLoan_shouldResponse(FeasibleCase testCase) {
        MortgageRate rate = new MortgageRate(testCase.request.getMaturityPeriod(), 3.5, Instant.now());

        when(mortgageRateRepository.findByMaturityPeriod(testCase.request.getMaturityPeriod())).thenReturn(
            Optional.of(rate));

        MortgageCheckResponse response = mortgageService.mortgageCheck(testCase.request);

        assertTrue(response.feasible());
        assertEquals(testCase.expectedMonthlyPayment, response.monthlyPayment());
        assertEquals("Mortgage is feasible.", response.message());
    }

    @Test
    void mortgageCheck_whenLoanExceedsIncomeLimit_shouldFail() {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(15);
        request.setIncome(BigDecimal.valueOf(20000));
        request.setLoanValue(BigDecimal.valueOf(90000));
        request.setHomeValue(BigDecimal.valueOf(100000));

        MortgageCheckResponse response = mortgageService.mortgageCheck(request);

        assertFalse(response.feasible());
        assertEquals(BigDecimal.ZERO, response.monthlyPayment());
        assertEquals("Loan value exceeds 4 times the income.", response.message());
    }

    @Test
    void mortgageCheck_whenLoanExceedsHomeValue_shouldFail() {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(30000));
        request.setLoanValue(BigDecimal.valueOf(120000));
        request.setHomeValue(BigDecimal.valueOf(100000));

        MortgageCheckResponse response = mortgageService.mortgageCheck(request);

        assertFalse(response.feasible());
        assertEquals(BigDecimal.ZERO, response.monthlyPayment());
        assertEquals("Loan value cannot exceed home value.", response.message());
    }

    @Test
    void mortgageCheck_whenNoRateFound_shouldFail() {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(25);
        request.setIncome(BigDecimal.valueOf(40000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(120000));

        when(mortgageRateRepository.findByMaturityPeriod(25)).thenReturn(Optional.empty());

        MortgageCheckResponse response = mortgageService.mortgageCheck(request);

        assertFalse(response.feasible());
        assertEquals(BigDecimal.ZERO, response.monthlyPayment());
        assertEquals("No interest rate found for the specified maturity period.", response.message());
    }
}