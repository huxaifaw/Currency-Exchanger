package com.currencyexchanger.domain.request;

import com.currencyexchanger.domain.ItemDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillDetailsRequest {
    private List<ItemDTO> items;
    private double totalAmount;
    private String userType;
    private int customerTenure;
    private String originalCurrency;
    private String targetCurrency;
}
