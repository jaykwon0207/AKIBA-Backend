package com.akiba.backend.used.domain;

public enum MarketSpecialType {
    NONE,
    SPECIAL_BENEFIT,
    LIMITED_EDITION,
    BOTH;

    public static MarketSpecialType fromInput(String input) {
        if (input == null || input.isBlank()) {
            return NONE;
        }

        String value = input.trim();
        return switch (value.toUpperCase()) {
            case "NONE", "없음" -> NONE;
            case "SPECIAL_BENEFIT", "특전" -> SPECIAL_BENEFIT;
            case "LIMITED_EDITION", "한정판" -> LIMITED_EDITION;
            case "BOTH", "특전+한정판", "특전/한정판" -> BOTH;
            default -> throw new IllegalArgumentException("유효하지 않은 specialType 값입니다.");
        };
    }
}
