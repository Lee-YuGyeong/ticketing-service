package com.yugyeong.ticketing_service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    USER, MANAGER, ADMIN;

    @JsonCreator
    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

}
