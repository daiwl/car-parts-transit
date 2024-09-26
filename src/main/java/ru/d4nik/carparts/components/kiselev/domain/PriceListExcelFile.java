package ru.d4nik.carparts.components.kiselev.domain;

import lombok.Builder;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;

@Builder
public record PriceListExcelFile(String fileName, Instant date, InputStream inputStream) {
}
