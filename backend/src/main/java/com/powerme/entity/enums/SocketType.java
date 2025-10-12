package com.powerme.entity.enums;

/**
 * Types de prises pour bornes de recharge électrique.
 */
public enum SocketType {
    TYPE_2S("Type 2S"),
    TYPE_2("Type 2"),
    CCS("CCS"),
    CHADEMO("CHAdeMO");

    private final String displayName;

    SocketType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
