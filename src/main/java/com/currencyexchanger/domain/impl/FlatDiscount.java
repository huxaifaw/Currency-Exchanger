package com.currencyexchanger.domain.impl;

import com.currencyexchanger.domain.Discount;
import com.currencyexchanger.domain.ItemDTO;

import java.util.List;

public class FlatDiscount implements Discount {
    @Override
    public double applyDiscount(List<ItemDTO> items, double totalBillAmount) {
        return ((int) totalBillAmount / 100 ) * 5d;
    }
}
