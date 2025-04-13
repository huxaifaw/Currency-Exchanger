package com.currencyexchanger.controller;

import com.currencyexchanger.domain.request.BillDetailsRequest;
import com.currencyexchanger.service.CurrencyExchangerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CurrencyExchangerController {

    private final CurrencyExchangerService currencyExchangeService;

    @PostMapping("/calculate")
    public ResponseEntity<Double> calculateNetPayableAmount(@RequestBody BillDetailsRequest billDetailsRequest) throws JsonProcessingException {
        double response = currencyExchangeService.calculatePayableAmount(billDetailsRequest);
        return ResponseEntity.ok(response);
    }
}
