package com.example.server.entities.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TaskStatus {
    WAITING("В ожидании"),
    IN_PROGRESS("В процессе"),
    DONE("Завершено");

    @Getter
    private final String value;
}
