package ru.d4nik.carparts.components.autocompass.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Product(Long id,
                      String article,
                      String name,
                      String brand,
                      Integer stocks,
                      BigDecimal price,
                      int quantityInSet) {
}
