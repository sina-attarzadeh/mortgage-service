package com.ing.mortgage.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ing.mortgage.model.MortgageRate;
import com.ing.mortgage.service.AnnuityMortgageServiceImpl;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InterestRateControllerIT {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    private AnnuityMortgageServiceImpl mortgageService;

    @Test
    void getInterestRates_withData_shouldReturnListOfRates() throws Exception {
        MortgageRate rate1 = new MortgageRate(10, 2.0, Instant.now());
        MortgageRate rate2 = new MortgageRate(20, 3.0, Instant.now());

        when(mortgageService.getInterestRates()).thenReturn(List.of(rate1, rate2));

        mockMvc.perform(get("/api/v1/interest-rates")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].maturityPeriod").value(10))
            .andExpect(jsonPath("$[1].maturityPeriod").value(20));
    }
}