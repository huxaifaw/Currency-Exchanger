package com.currencyexchanger.service;

import com.currencyexchanger.domain.response.ExchangeRateApiResponseDTO;
import com.currencyexchanger.exceptions.CurrencyConversionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ExchangeRateClient exchangeRateClient;

    private final String baseUrl = "https://temp.com";
    private final String endpoint = "/convert";

    @BeforeEach
    void setUp() {
        exchangeRateClient = new ExchangeRateClient(restTemplate, objectMapper);
    }

    @Test
    void testGetConversionRate_Success() throws Exception {
        String baseCode = "USD";
        String targetCode = "PKR";
        String responseJson = "{\"result\": success, \"base_code\": USD, \"target_code\": PKR, \"conversion_rate\": 280.7}";

        ExchangeRateApiResponseDTO expectedResponse = new ExchangeRateApiResponseDTO();
        expectedResponse.setConversionRate(0.85);

        String url = baseUrl + endpoint + "/" + baseCode + "/" + targetCode;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), (HttpEntity<?>) any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        when(objectMapper.readValue(responseJson, ExchangeRateApiResponseDTO.class)).thenReturn(expectedResponse);
        ReflectionTestUtils.setField(exchangeRateClient, "exchangeRateApi", baseUrl);
        ReflectionTestUtils.setField(exchangeRateClient, "pairConversionEndpoint", endpoint);

        ExchangeRateApiResponseDTO actualResponse = exchangeRateClient.getConversionRate(baseCode, targetCode);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getConversionRate(), actualResponse.getConversionRate());

        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), (HttpEntity<?>) any(HttpEntity.class), eq(String.class));
        verify(objectMapper, times(1)).readValue(responseJson, ExchangeRateApiResponseDTO.class);
    }

    @Test
    void testGetConversionRate_RestClientException() {
        String baseCode = "USD";
        String targetCode = "INVALID";
        String url = baseUrl + endpoint + "/" + baseCode + "/" + targetCode;

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), (HttpEntity<?>) any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("API error"));
        ReflectionTestUtils.setField(exchangeRateClient, "exchangeRateApi", baseUrl);
        ReflectionTestUtils.setField(exchangeRateClient, "pairConversionEndpoint", endpoint);

        CurrencyConversionException exception = assertThrows(CurrencyConversionException.class, () ->
                exchangeRateClient.getConversionRate(baseCode, targetCode));

        assertTrue(exception.getMessage().contains("Failed to retrieve exchange rate"));
    }

    @Test
    void testGetConversionRate_JsonProcessingException() throws Exception {
        String baseCode = "USD";
        String targetCode = "EUR";
        String responseJson = "invalid_json";

        String url = baseUrl + endpoint + "/" + baseCode + "/" + targetCode;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseJson, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), (HttpEntity<?>) any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);
        when(objectMapper.readValue(responseJson, ExchangeRateApiResponseDTO.class))
                .thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Error") {});
        ReflectionTestUtils.setField(exchangeRateClient, "exchangeRateApi", baseUrl);
        ReflectionTestUtils.setField(exchangeRateClient, "pairConversionEndpoint", endpoint);

        CurrencyConversionException exception = assertThrows(CurrencyConversionException.class, () ->
                exchangeRateClient.getConversionRate(baseCode, targetCode));

        assertTrue(exception.getMessage().contains("Failed to parse exchange rate response"));
    }
}