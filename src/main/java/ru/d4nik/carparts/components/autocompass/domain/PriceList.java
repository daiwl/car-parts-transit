package ru.d4nik.carparts.components.autocompass.domain;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PriceList(Instant date, List<Product> products) {
}
