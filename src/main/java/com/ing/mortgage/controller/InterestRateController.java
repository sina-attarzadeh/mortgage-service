package com.ing.mortgage.controller;

import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.service.AnnuityMortgageServiceImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle interest rate related endpoints.
 */
@RestController
@RequiredArgsConstructor
public class InterestRateController {

    private final AnnuityMortgageServiceImpl mortgageService;

    @GetMapping("/api/v1/interest-rates")
    ResponseEntity<List<MortgageRate>> getInterestRates() {
        return new ResponseEntity<>(mortgageService.getInterestRates(), HttpStatus.OK);
    }
}
