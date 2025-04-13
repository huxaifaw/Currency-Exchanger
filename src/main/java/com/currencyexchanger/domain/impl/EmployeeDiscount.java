package com.currencyexchanger.domain.impl;

import com.currencyexchanger.domain.Discount;
import com.currencyexchanger.domain.ItemDTO;

import java.util.List;

public class EmployeeDiscount implements Discount {
    @Override
    public double applyDiscount(List<ItemDTO> items, double totalBillAmount) {
        return calculateDiscount(items, 30);
    }
}
