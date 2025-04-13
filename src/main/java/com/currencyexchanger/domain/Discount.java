package com.currencyexchanger.domain;

import com.currencyexchanger.domain.enums.Category;

import java.util.List;

public interface Discount {
    double applyDiscount(List<ItemDTO> items, double totalBillAmount);
    default double calculateDiscount(List<ItemDTO> items, int percent) {
        double nonGroceryItemsTotal = items.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(Category.NON_GROCERY.getName()))
                .mapToDouble(ItemDTO::getPrice)
                .sum();
        return nonGroceryItemsTotal * percent / 100.0;
    }
}
