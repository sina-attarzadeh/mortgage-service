package com.ing.mortgage.controller;

import com.ing.mortgage.domain.MortgageCheckResponse;
import com.ing.mortgage.domain.MortgageRequest;
import com.ing.mortgage.service.AnnuityMortgageServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle mortgage related endpoints.
 */
@RestController
@RequiredArgsConstructor
public class MortgageController {

    private final AnnuityMortgageServiceImpl mortgageService;

    @PostMapping("/api/v1/mortgage-check")
    ResponseEntity<MortgageCheckResponse> mortgageCheck(@Valid @RequestBody MortgageRequest mortgageRequest) {
        return new ResponseEntity<>(mortgageService.mortgageCheck(mortgageRequest), HttpStatus.OK);
    }
}
