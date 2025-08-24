package com.ing.mortgage.service;

import com.ing.mortgage.domain.MortgageCheckResponse;
import com.ing.mortgage.domain.MortgageRequest;
import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.repository.MortgageRateRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to handle mortgage-related operations such as retrieving interest rates and checking annuity mortgage
 * feasibility.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnnuityMortgageServiceImpl implements MortgageService {

    private final MortgageRateRepository mortgageRateRepository;

    /**
     * Get all available mortgage interest rates.
     *
     * @return List of all available mortgage interest rates.
     */
    @Override
    public List<MortgageRate> getInterestRates() {
        return mortgageRateRepository.findAll();
    }

    /**
     * Check the feasibility of a mortgage request and calculate the monthly payment if feasible. This method checks if
     * the mortgage request is feasible based on the following criteria:
     *     <ul>
     *     <li>The loan value must not exceed 4 times the income.</li>
     *     <li>The loan value must not exceed the home value.</li>
     *     <li>An interest rate must exist for the specified maturity period.</li>
     *     </ul>
     * If all criteria are met, it calculates the monthly payment.
     *
     * @param mortgageRequest user request for mortgage
     * @return MortgageCheckResponse containing the feasibility of the mortgage, monthly payment, and a message.
     */
    @Override
    public MortgageCheckResponse mortgageCheck(MortgageRequest mortgageRequest) {
        if (mortgageRequest.getLoanValue().compareTo(mortgageRequest.getIncome().multiply(BigDecimal.valueOf(4))) > 0) {
            log.debug("Loan value exceeds 4 times the income for {}", mortgageRequest);
            return new MortgageCheckResponse(false, BigDecimal.ZERO, "Loan value exceeds 4 times the income.");
        }

        if (mortgageRequest.getLoanValue().compareTo(mortgageRequest.getHomeValue()) > 0) {
            log.debug("Loan value cannot exceed home value for {}", mortgageRequest);
            return new MortgageCheckResponse(false, BigDecimal.ZERO, "Loan value cannot exceed home value.");
        }

        Optional<MortgageRate> mortgageRate = mortgageRateRepository.findByMaturityPeriod(
            mortgageRequest.getMaturityPeriod());

        if (mortgageRate.isEmpty()) {
            log.debug("No interest rate found for the specified maturity period for {}", mortgageRequest);
            return new MortgageCheckResponse(false, BigDecimal.ZERO,
                "No interest rate found for the specified maturity period.");
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(mortgageRequest, mortgageRate.get());

        return new MortgageCheckResponse(true, monthlyPayment, "Mortgage is feasible.");
    }

    /**
     * Calculate the monthly payment for the mortgage based on the loan value, maturity period, and interest rate. If
     * the interest rate is zero, it returns a simple division of the loan value by the total number of months in the
     * maturity period. If the interest rate is non-zero, it uses the formula for calculating the monthly payment for an
     * amortizing loan:
     * <br>
     * M = P[r(1 + r)^n] / [(1 + r)^n - 1]
     * <br>
     * Where:
     * <br> M = monthly payment
     * <br> P = loan principal (loan value)
     * <br> r = monthly interest rate (annual interest rate / 12 / 100)
     * <br> n = total number of payments (maturity period in months)
     *
     * @param mortgageRequest user request for mortgage
     * @param mortgageRate    interest rate for the mortgage based on maturity period
     * @return monthly payment for the mortgage.
     */
    private BigDecimal calculateMonthlyPayment(MortgageRequest mortgageRequest, MortgageRate mortgageRate) {
        BigDecimal loanValue = mortgageRequest.getLoanValue();
        int totalMonths = mortgageRequest.getMaturityPeriod() * 12;
        double annualRate = mortgageRate.interestRate();
        double monthlyRate = annualRate / 12 / 100;

        if (monthlyRate == 0) {
            return loanValue.divide(BigDecimal.valueOf(totalMonths), 2, RoundingMode.HALF_UP);
        }

        double pow = Math.pow(1 + monthlyRate, totalMonths);
        double monthlyPayment = loanValue.doubleValue() * (monthlyRate * pow / (pow - 1));

        BigDecimal result = BigDecimal.valueOf(monthlyPayment).setScale(2, RoundingMode.HALF_UP);

        log.debug("Monthly payment is {} with mortgageRequest {} and mortgageRate {}", result, mortgageRequest,
            mortgageRate);

        return result;
    }
}
