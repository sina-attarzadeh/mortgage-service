package com.ing.mortgage.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.mortgage.domain.MortgageCheckResponse;
import com.ing.mortgage.domain.MortgageRequest;
import com.ing.mortgage.service.AnnuityMortgageServiceImpl;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MortgageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnnuityMortgageServiceImpl mortgageService;

    @Test
    void mortgageCheck_feasibleLoan_shouldResponse() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(20);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        MortgageCheckResponse response = new MortgageCheckResponse(true, BigDecimal.valueOf(579.96),
            "Mortgage is feasible.");

        when(mortgageService.mortgageCheck(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feasible").value(true))
            .andExpect(jsonPath("$.monthlyPayment").value(579.96))
            .andExpect(jsonPath("$.message").value("Mortgage is feasible."));
    }

    @Test
    void mortgageCheck_withExceededLoan_shouldReturnNotFeasibleResponse() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(20);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(200000));
        request.setHomeValue(BigDecimal.valueOf(200000));

        MortgageCheckResponse response = new MortgageCheckResponse(false, BigDecimal.ZERO,
            "Loan value exceeds 4 times the income.");

        when(mortgageService.mortgageCheck(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feasible").value(false))
            .andExpect(jsonPath("$.monthlyPayment").value(0))
            .andExpect(jsonPath("$.message").value("Loan value exceeds 4 times the income."));
    }

    @Test
    void mortgageCheck_withExceededHomeValue_shouldReturnNotFeasibleResponse() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(20);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(90000));

        MortgageCheckResponse response = new MortgageCheckResponse(false, BigDecimal.ZERO,
            "Loan value cannot exceed home value.");

        when(mortgageService.mortgageCheck(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feasible").value(false))
            .andExpect(jsonPath("$.monthlyPayment").value(0))
            .andExpect(jsonPath("$.message").value("Loan value cannot exceed home value."));
    }

    @Test
    void mortgageCheck_withNoMaturityPeriod_shouldReturnNotFeasibleResponse() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(40);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(90000));

        MortgageCheckResponse response = new MortgageCheckResponse(false, BigDecimal.ZERO,
            "No interest rate found for the specified maturity period.");

        when(mortgageService.mortgageCheck(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feasible").value(false))
            .andExpect(jsonPath("$.monthlyPayment").value(0))
            .andExpect(jsonPath("$.message").value("No interest rate found for the specified maturity period."));
    }

    @Test
    void mortgageCheck_withNullMaturityPeriod_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(null);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("maturityPeriod: must not be null"));
    }

    @Test
    void mortgageCheck_withInvalidMaturityPeriod_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(0);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("maturityPeriod: must be greater than or equal to 1"));
    }

    @Test
    void mortgageCheck_withNullIncome_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(null);
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("income: must not be null"));
    }

    @Test
    void mortgageCheck_withInvalidIncome_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(-50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("income: must be greater than or equal to 0"));
    }

    @Test
    void mortgageCheck_withNullLoanValue_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(null);
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("loanValue: must not be null"));
    }

    @Test
    void mortgageCheck_withInvalidLoanValue_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(-100000));
        request.setHomeValue(BigDecimal.valueOf(150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("loanValue: must be greater than or equal to 1"));
    }

    @Test
    void mortgageCheck_withNullHomeValue_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(null);

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("homeValue: must not be null"));
    }

    @Test
    void mortgageCheck_withInvalidHomeValue_shouldReturnBadRequest() throws Exception {
        MortgageRequest request = new MortgageRequest();
        request.setMaturityPeriod(10);
        request.setIncome(BigDecimal.valueOf(50000));
        request.setLoanValue(BigDecimal.valueOf(100000));
        request.setHomeValue(BigDecimal.valueOf(-150000));

        mockMvc.perform(post("/api/v1/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.key").value("validation_error"))
            .andExpect(jsonPath("$.message").value("homeValue: must be greater than or equal to 1"));
    }
}