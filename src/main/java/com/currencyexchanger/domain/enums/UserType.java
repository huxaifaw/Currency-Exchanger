package com.currencyexchanger.domain.enums;

import lombok.Getter;

@Getter
public enum UserType {
    EMPLOYEE("employee"), AFFILIATE("affiliate"), CUSTOMER("customer");

    private String value;

    UserType(String value) {
        this.value = value;
    }
}
