package com.currencyexchanger.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum UserType {
    EMPLOYEE("employee"), AFFILIATE("affiliate"), CUSTOMER("customer");

    private String value;

    UserType(String value) {
        this.value = value;
    }

    public static Optional<UserType> fromString(String value) {
        return Arrays.stream(UserType.values())
                .filter(type -> type.getValue().equalsIgnoreCase(value))
                .findFirst();
    }
}
