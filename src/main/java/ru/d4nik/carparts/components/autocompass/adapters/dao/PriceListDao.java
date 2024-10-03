package ru.d4nik.carparts.components.autocompass.adapters.dao;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.d4nik.carparts.components.autocompass.adapters.dao.records.ProductRecord;
import ru.d4nik.carparts.components.autocompass.domain.PriceList;
import ru.d4nik.carparts.components.autocompass.domain.Product;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PriceListDao {
    static final BeanPropertyRowMapper<ProductRecord> ROW_MAPPER = new BeanPropertyRowMapper<>(ProductRecord.class);
    public static final ZoneId UTC = ZoneId.of("UTC");

    NamedParameterJdbcOperations jdbcOperations;

    @Transactional
    public void save(PriceList priceList) {
        Timestamp updated = Timestamp.from(priceList.date());

        SqlParameterSource[] params = priceList.products().stream()
                .map(product -> toParams(product, updated))
                .toArray(SqlParameterSource[]::new);

        jdbcOperations.update("DELETE FROM kiselev_price_list WHERE id > 0", Map.of());

        jdbcOperations.batchUpdate("""
                INSERT INTO kiselev_price_list (article, name, brand, price, stocks, quantity_in_set, updated_at)
                VALUES (:article, :name, :brand, :price, :stocks, :quantity_in_set, :updated_at)
                """, params);
    }

    public PriceList loadPriceList() {
        var records = jdbcOperations.query("SELECT * FROM kiselev_price_list", Map.of(), ROW_MAPPER);
        if (records.isEmpty()) {
            throw new RuntimeException("Нет данных прайс-листа");
        }
        return PriceList.builder()
                .date(records.get(0).getUpdatedAt())
                .products(records.stream().map(this::toProduct).toList())
                .build();
    }

    private Product toProduct(ProductRecord productRecord) {
        return Product.builder()
                .id(productRecord.getId())
                .article(productRecord.getArticle())
                .name(productRecord.getName())
                .brand(productRecord.getBrand())
                .stocks(productRecord.getStocks())
                .price(productRecord.getPrice())
                .quantityInSet(productRecord.getQuantityInSet())
                .build();
    }

    private MapSqlParameterSource toParams(Product product, Timestamp updated) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("article", product.article());
        params.addValue("name", product.name());
        params.addValue("brand", product.brand());
        params.addValue("price", product.price());
        params.addValue("stocks", product.stocks());
        params.addValue("quantity_in_set", product.quantityInSet());
        params.addValue("updated_at", updated);
        return params;
    }
}
