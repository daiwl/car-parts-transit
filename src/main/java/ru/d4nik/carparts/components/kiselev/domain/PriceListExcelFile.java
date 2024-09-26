package ru.d4nik.carparts.components.kiselev.domain;

import lombok.Builder;

import java.io.InputStream;
import java.time.Instant;

@Builder
public record PriceListExcelFile(String fileName, Instant date, InputStream inputStream) {
}
