package com.currencyexchanger.service;

import com.currencyexchanger.domain.Discount;
import com.currencyexchanger.domain.enums.UserType;
import com.currencyexchanger.domain.impl.AffiliateDiscount;
import com.currencyexchanger.domain.impl.EmployeeDiscount;
import com.currencyexchanger.domain.impl.FlatDiscount;
import com.currencyexchanger.domain.impl.LoyaltyDiscount;
import com.currencyexchanger.domain.request.BillDetailsRequest;
import com.currencyexchanger.domain.response.ExchangeRateApiResponseDTO;
import com.currencyexchanger.exceptions.CurrencyConversionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyExchangerService {

    private final ExchangeRateClient exchangeRateClient;

    public double calculatePayableAmount(BillDetailsRequest billDetailsRequest) {
        validateBillDetails(billDetailsRequest);

        ExchangeRateApiResponseDTO exchangeRateApiResponseDTO;
        exchangeRateApiResponseDTO = exchangeRateClient.getConversionRate(billDetailsRequest.getOriginalCurrency(), billDetailsRequest.getTargetCurrency());

        double totalBillAmount = getConvertedBillAmount(billDetailsRequest.getTotalAmount(), exchangeRateApiResponseDTO.getConversionRate());
        double nonGroceryItemsDiscount = getConvertedBillAmount(getPercentageDiscounts(billDetailsRequest), exchangeRateApiResponseDTO.getConversionRate());
        double flatDiscount = getConvertedBillAmount(getFlatDiscount(billDetailsRequest), exchangeRateApiResponseDTO.getConversionRate());

        return totalBillAmount - nonGroceryItemsDiscount - flatDiscount;
    }

    private void validateBillDetails(BillDetailsRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new CurrencyConversionException("Items list cannot be null or empty");
        }
        if (request.getOriginalCurrency() == null || request.getTargetCurrency() == null) {
            throw new CurrencyConversionException("Original Currency or Target Currency is null");
        }
        if (request.getTotalAmount() <= 0) {
            throw new CurrencyConversionException("Total amount must be greater than zero");
        }
        if (request.getCustomerTenure() < 0) {
            throw new CurrencyConversionException("Customer tenure must greater than or equal to zero");
        }
        if (request.getUserType() == null || request.getUserType().isEmpty()) {
            throw new CurrencyConversionException("User Type can't be null or empty");
        }
        Optional<UserType> userTypeOptional = UserType.fromString(request.getUserType());
        if (userTypeOptional.isEmpty()) {
            throw new CurrencyConversionException("Invalid user type provided");
        }
    }

    private double getConvertedBillAmount(double billAmount, double conversionRate) {
        return billAmount * conversionRate;
    }

    private double getPercentageDiscounts(BillDetailsRequest billDetailsRequest) {
        double nonGroceryItemsDiscount = 0;
        Discount discount;
        if (UserType.EMPLOYEE.getValue().equals(billDetailsRequest.getUserType())) {
            discount = new EmployeeDiscount();
            nonGroceryItemsDiscount = discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
        } else if (UserType.AFFILIATE.getValue().equals(billDetailsRequest.getUserType())) {
            discount = new AffiliateDiscount();
            nonGroceryItemsDiscount = discount.applyDiscount(billDetailsRequest.getItems(), billDetailsRequest.getTotalAmount());
        } else if (UserType.CUSTOMER.getValue().equals(billDetailsRequest.getUserType()) && billDetailsRequest.getCustomerTenure() >= 2) {
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
