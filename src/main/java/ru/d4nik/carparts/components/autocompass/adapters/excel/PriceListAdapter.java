package ru.d4nik.carparts.components.autocompass.adapters.excel;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import ru.d4nik.carparts.components.autocompass.adapters.excel.model.ExcelDataSheet;
import ru.d4nik.carparts.components.autocompass.domain.Product;
import ru.d4nik.carparts.components.autocompass.domain.PriceListExcelFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceListAdapter {

    public static final String BRAND = "Бренд";
    private static final int IMPORT_SHEET_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 7;
    public static final String ARTICLE = "Артикул";
    public static final String NAME = "Наименование";
    public static final String STOCKS = "Остаток";
    public static final String COST = "Цена";

    public List<Product> parse(PriceListExcelFile file) {
        var excelDataSheet = getExcelDataSheet(file);
        return excelDataSheet.readData().stream()
                .map(this::mapRowToProduct)
                .toList();
    }

    private Product mapRowToProduct(ImmutableMap<String, String> row) {
        return Product.builder()
                .article(row.get(ARTICLE))
                .name(row.get(NAME))
                .brand(row.get(BRAND))
                .stocks(new BigDecimal(row.get(STOCKS).replace(",", "")).intValue())
                .price(new BigDecimal(row.get(COST).replace(",", "")).setScale(2))
                .quantityInSet(1)
                .build();
    }

    private ExcelDataSheet getExcelDataSheet(PriceListExcelFile file) {
        try (var inputStream = file.inputStream()) {
            try (var workbook = WorkbookFactory.create(inputStream)) {
                var firstSheet = workbook.getSheetAt(IMPORT_SHEET_INDEX);
                return ExcelDataSheet.create(firstSheet, DATA_START_ROW_INDEX, PROPERTY_NAME_TO_INDEX_MAP);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private final static ImmutableMap<String, Integer> PROPERTY_NAME_TO_INDEX_MAP = ImmutableMap.<String, Integer>builder()
            .put(ARTICLE, 0)
            .put(NAME, 7)
            .put(BRAND, 13)
            .put(COST, 14)
            .put(STOCKS, 16)
            .build();
}
