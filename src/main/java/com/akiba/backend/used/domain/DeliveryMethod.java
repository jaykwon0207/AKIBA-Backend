package com.akiba.backend.used.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum DeliveryMethod {
    DELIVERY("택배"),
    DIRECT("직거래");

    private final String label;

    DeliveryMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static DeliveryMethod fromInput(String value) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryMethod는 필수입니다.");
        }
        return switch (value) {
            case "택배", "DELIVERY" -> DELIVERY;
            case "직거래", "DIRECT" -> DIRECT;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 deliveryMethod 값입니다.");
        };
    }
}
