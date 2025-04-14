package com.currencyexchanger.service;


import com.currencyexchanger.domain.ItemDTO;
import com.currencyexchanger.domain.request.BillDetailsRequest;
import com.currencyexchanger.domain.response.ExchangeRateApiResponseDTO;
import com.currencyexchanger.exceptions.CurrencyConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CurrencyExchangerServiceTest {

    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private CurrencyExchangerService currencyExchangerService;

    private List<ItemDTO> items;

    @BeforeEach
    void setup() {
        items = List.of(
                new ItemDTO("Laptop", "non_grocery", 1050),
                new ItemDTO("Mouse", "non_grocery", 250),
                new ItemDTO("Apples", "grocery", 155)
        );
    }

    @Test
    void testCalculatePayableAmount_forEmployee_shouldApplyDiscountsCorrectly() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency("USD")
                .targetCurrency("EUR")
                .totalAmount(1455)
                .items(items)
                .userType("employee")
                .customerTenure(1)
                .build();

        ExchangeRateApiResponseDTO mockResponse = new ExchangeRateApiResponseDTO();
        mockResponse.setConversionRate(1); // Example rate

        Mockito.when(exchangeRateClient.getConversionRate("USD", "EUR"))
                .thenReturn(mockResponse);

        double payable = currencyExchangerService.calculatePayableAmount(request);

        Assertions.assertEquals(995, payable);
    }

    @Test
    void testCalculatePayableAmount_forAffiliate_shouldApplyDiscountsCorrectly() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency("USD")
                .targetCurrency("EUR")
                .totalAmount(1455)
                .items(items)
                .userType("affiliate")
                .customerTenure(1)
                .build();

        ExchangeRateApiResponseDTO mockResponse = new ExchangeRateApiResponseDTO();
        mockResponse.setConversionRate(1);

        Mockito.when(exchangeRateClient.getConversionRate("USD", "EUR"))
                .thenReturn(mockResponse);

        double payable = currencyExchangerService.calculatePayableAmount(request);

        Assertions.assertEquals(1125, payable);
    }

    @Test
    void testCalculatePayableAmount_loyalCustomer_shouldApplyLoyaltyDiscount() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency("USD")
                .targetCurrency("EUR")
                .totalAmount(1455)
                .items(items)
                .userType("customer")
                .customerTenure(3)
                .build();

        ExchangeRateApiResponseDTO mockResponse = new ExchangeRateApiResponseDTO();
        mockResponse.setConversionRate(1);

        Mockito.when(exchangeRateClient.getConversionRate("USD", "EUR"))
                .thenReturn(mockResponse);

        double payable = currencyExchangerService.calculatePayableAmount(request);

        Assertions.assertEquals(1320, payable);
    }

    @Test
    void testCalculatePayableAmount_invalidCurrency_shouldThrowException() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency(null)
                .targetCurrency("EUR")
                .totalAmount(100)
                .items(items)
                .userType("employee")
                .customerTenure(2)
                .build();

        CurrencyConversionException exception = Assertions.assertThrows(
                CurrencyConversionException.class,
                () -> currencyExchangerService.calculatePayableAmount(request)
        );

        Assertions.assertEquals("Original Currency or Target Currency is null", exception.getMessage());
    }

    @Test
    void testCalculatePayableAmount_emptyItems_shouldThrowException() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency("USD")
                .targetCurrency("EUR")
                .totalAmount(100)
                .items(new ArrayList<>())
                .userType("employee")
                .customerTenure(2)
                .build();

        CurrencyConversionException exception = Assertions.assertThrows(
                CurrencyConversionException.class,
                () -> currencyExchangerService.calculatePayableAmount(request)
        );

        Assertions.assertEquals("Items list cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCalculatePayableAmount_invalidUserType_shouldThrowException() {
        BillDetailsRequest request = BillDetailsRequest.builder()
                .originalCurrency("USD")
                .targetCurrency("EUR")
                .totalAmount(100)
                .items(items)
                .userType("user")
                .customerTenure(2)
                .build();

        CurrencyConversionException exception = Assertions.assertThrows(
                CurrencyConversionException.class,
                () -> currencyExchangerService.calculatePayableAmount(request)
        );

        Assertions.assertEquals("Invalid user type provided", exception.getMessage());
    }
}
