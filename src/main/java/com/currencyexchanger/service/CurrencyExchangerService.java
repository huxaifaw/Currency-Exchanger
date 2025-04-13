package com.currencyexchanger.service;

import com.currencyexchanger.domain.Discount;
import com.currencyexchanger.domain.enums.UserType;
import com.currencyexchanger.domain.impl.AffiliateDiscount;
import com.currencyexchanger.domain.impl.EmployeeDiscount;
import com.currencyexchanger.domain.impl.FlatDiscount;
import com.currencyexchanger.domain.impl.LoyaltyDiscount;
import com.currencyexchanger.domain.request.BillDetailsRequest;
import com.currencyexchanger.domain.response.ExchangeRateApiResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CurrencyExchangerService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${currency-exchanger.config.exchange-rate.api-url}")
    private String exchangeRateApi;
    @Value("${currency-exchanger.config.exchange-rate.endpoint.pair-conversion}")
    private String pairConversionEndpoint;

    public double calculatePayableAmount(BillDetailsRequest billDetailsRequest) throws IllegalArgumentException, JsonProcessingException {

        ExchangeRateApiResponseDTO exchangeRateApiResponseDTO;
        if (Objects.nonNull(billDetailsRequest.getOriginalCurrency()) && Objects.nonNull(billDetailsRequest.getTargetCurrency())) {
            exchangeRateApiResponseDTO = getConversionRate(billDetailsRequest.getOriginalCurrency(), billDetailsRequest.getTargetCurrency());
        } else {
            throw new IllegalArgumentException("Original Currency or Target Currency is null");
        }
        double totalBillAmount = getConvertedBillAmount(billDetailsRequest.getTotalAmount(), exchangeRateApiResponseDTO.getConversionRate());
        double nonGroceryItemsDiscount = getConvertedBillAmount(getPercentageDiscounts(billDetailsRequest), exchangeRateApiResponseDTO.getConversionRate());
        double flatDiscount = getConvertedBillAmount(getFlatDiscount(billDetailsRequest), exchangeRateApiResponseDTO.getConversionRate());

        return totalBillAmount - nonGroceryItemsDiscount - flatDiscount;
    }

    private ExchangeRateApiResponseDTO getConversionRate(String baseCode, String targetCode) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> request = new HttpEntity<>(headers);
        String exchangeRateApiResponse = restTemplate.exchange(exchangeRateApi + pairConversionEndpoint + "/" + baseCode + "/" +targetCode, HttpMethod.GET, request, String.class)
                .getBody();

        return objectMapper.readValue(exchangeRateApiResponse, ExchangeRateApiResponseDTO.class);
    }

    private double getConvertedBillAmount(double billAmount, double conversionRate) {
        return billAmount * conversionRate;
    }

    private double getPercentageDiscounts(BillDetailsRequest billDetailsRequest) {
        double nonGroceryItemsDiscount = 0;
        Discount discount;
        if (UserType.EMPLOYEE.getValue().equalsIgnoreCase(billDetailsRequest.getUserType())) {
            discount = new EmployeeDiscount();
            nonGroceryItemsDiscount = discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
        } else if (UserType.AFFILIATE.getValue().equalsIgnoreCase(billDetailsRequest.getUserType())) {
            discount = new AffiliateDiscount();
            nonGroceryItemsDiscount = discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
        } else if (UserType.CUSTOMER.getValue().equalsIgnoreCase(billDetailsRequest.getUserType()) && billDetailsRequest.getCustomerTenure() >= 2) {
            discount = new LoyaltyDiscount();
            nonGroceryItemsDiscount = discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
        }
        return nonGroceryItemsDiscount;
    }

    private double getFlatDiscount(BillDetailsRequest billDetailsRequest) {
        Discount discount = new FlatDiscount();
        return discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
    }
}
