package com.currencyexchanger.domain.enums;

import lombok.Getter;

@Getter
public enum Category {
    GROCERY("grocery"), NON_GROCERY("non_grocery");

    private String name;

    Category(String name) {
        this.name = name;
    }
}
