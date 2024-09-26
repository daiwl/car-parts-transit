package ru.d4nik.carparts.components.kiselev.adapters.dao.records;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
public final class ProductRecord {
    private Long id;
    private String article;
    private String name;
    private String brand;
    private Integer stocks;
    private BigDecimal price;
    private Integer quantityInSet;
    private Instant updatedAt;
}
