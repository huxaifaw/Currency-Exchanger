package com.currencyexchanger.service;

import com.currencyexchanger.domain.response.ExchangeRateApiResponseDTO;
import com.currencyexchanger.exceptions.CurrencyConversionException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ExchangeRateClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${currency-exchanger.config.exchange-rate.api-url}")
    private String exchangeRateApi;
    @Value("${currency-exchanger.config.exchange-rate.endpoint.pair-conversion}")
    private String pairConversionEndpoint;

    public ExchangeRateApiResponseDTO getConversionRate(String baseCode, String targetCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Object> request = new HttpEntity<>(headers);
            String exchangeRateApiResponse = restTemplate.exchange(exchangeRateApi + pairConversionEndpoint + "/" + baseCode + "/" + targetCode, HttpMethod.GET, request, String.class)
                    .getBody();

            return objectMapper.readValue(exchangeRateApiResponse, ExchangeRateApiResponseDTO.class);
        }
        catch (RestClientException ex) {
            throw new CurrencyConversionException("Failed to retrieve exchange rate: " + ex.getMessage(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new CurrencyConversionException("Failed to parse exchange rate response", ex);
        }
    }
}
