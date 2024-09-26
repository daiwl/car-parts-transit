package ru.d4nik.carparts.components.kiselev.adapters.excel;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import ru.d4nik.carparts.components.kiselev.adapters.excel.model.ExcelDataSheet;
import ru.d4nik.carparts.components.kiselev.domain.PriceListExcelFile;
import ru.d4nik.carparts.components.kiselev.domain.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceListAdapter {

    private static final int IMPORT_SHEET_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 7;

    public List<Product> parse(PriceListExcelFile file) {
        var excelDataSheet = getExcelDataSheet(file);
        return excelDataSheet.readData().stream()
                .map(this::mapToProduct)
                .toList();
    }

    private Product mapToProduct(ImmutableMap<String, String> row) {
        return Product.builder()
                .article(row.get("Артикул"))
                .name(row.get("Наименование"))
                .brand(row.get("Бренд"))
                .stocks(new BigDecimal(row.get("Остаток").replace(",", "")).intValue())
                .price(new BigDecimal(row.get("Цена").replace(",", "")).setScale(2))
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
            .put("Артикул", 0)
            .put("Наименование", 7)
            .put("Бренд", 13)
            .put("Цена", 14)
            .put("Остаток", 16)
            .build();
}
