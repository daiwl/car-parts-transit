package ru.d4nik.carparts.components.kiselev.adapters.excel.model;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nullable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.d4nik.carparts.shared.utils.HtmlUtils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Лист Excel для работы с данными.
 * Лист Excel должен содержать информацию в следующем формате:
 * <ul>
 *    <li>Должна быть строка, содержащая имена свойств.
 *    <li>Начиная с какого-то номера строки все последующие строки предназначены для хранения данных.
 * </ul>
 */
public final class ExcelDataSheet {

    private final Sheet sheet;
    private final int dataStartRowIndex;
    private final ImmutableMap<String, Integer> propertyNameToIndexMap;

    /**
     * Создание листа данных на основе исходного листа Excel.
     *
     * @param sheet              Лист Excel
     * @param propertiesRowIndex Индекс строки, содержащей имена свойст
     * @param dataStartRowIndex  Индекс стартовой строки с данными
     */
    public static ExcelDataSheet create(Sheet sheet, int propertiesRowIndex, int dataStartRowIndex) {

        checkArgument(sheet != null);
        checkArgument(dataStartRowIndex > propertiesRowIndex);

        var propertyNameToIndexMap = createPropertyMapping(sheet, propertiesRowIndex);
        return new ExcelDataSheet(sheet, dataStartRowIndex, propertyNameToIndexMap);
    }

    public static ExcelDataSheet create(Sheet sheet, int dataStartRowIndex, ImmutableMap<String, Integer> propertyNameToIndexMap) {
        checkArgument(sheet != null);
        return new ExcelDataSheet(sheet, dataStartRowIndex, propertyNameToIndexMap);
    }

    private ExcelDataSheet(
            Sheet sheet,
            int dataStartRowIndex,
            ImmutableMap<String, Integer> propertyNameToIndexMap) {

        this.sheet = sheet;
        this.dataStartRowIndex = dataStartRowIndex;
        this.propertyNameToIndexMap = propertyNameToIndexMap;
    }

    /**
     * Возвращает список строк с данными. Каждая строка представляет собой карту (имя свойства -> значение).
     * Не заполненные ячейки и ячейки с пустым значениеи игнорируются.
     */
    public ImmutableList<ImmutableMap<String, String>> readData() {

        var result = ImmutableList.<ImmutableMap<String, String>>builder();
        for (int i = dataStartRowIndex; i <= sheet.getLastRowNum(); i++) {

            var row = sheet.getRow(i);
            var dataRow = readDataRow(row);

            // Считаем, что строки с данными должны идти подряд
            // Если встретили пустую строку - значит данные закончились и продолжать нет смысла
            if (dataRow.isEmpty())
                break;

            result.add(dataRow);
        }

        return result.build();
    }

    /**
     * Записывает данные на лист начиная с первой строки данных.
     *
     * @param dataRows Список строк данных
     */
    public void writeData(ImmutableList<ImmutableMap<String, String>> dataRows) {

        var rowIndex = dataStartRowIndex;

        for (var dataRow : dataRows) {

            var row = sheet.getRow(rowIndex);
            if (row == null)
                row = sheet.createRow(rowIndex);

            writeDataRow(row, dataRow);

            rowIndex++;
        }
    }

    private ImmutableMap<String, String> readDataRow(@Nullable Row row) {

        if (row == null)
            return ImmutableMap.of();

        var result = ImmutableMap.<String, String>builder();

        for (var entry : propertyNameToIndexMap.entrySet()) {

            var index = entry.getValue();
            var propertyName = entry.getKey();

            Cell cell = row.getCell(index, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null) {
                String value = getCellValueAsPlainString(cell);
                if (value != null && !value.isEmpty())
                    result.put(propertyName, value);
            }
        }

        return result.build();
    }

    private void writeDataRow(Row row, ImmutableMap<String, String> dataRow) {

        for (var entry : propertyNameToIndexMap.entrySet()) {

            var columnIndex = entry.getValue();
            var propertyName = entry.getKey();

            var propertyValue = dataRow.get(propertyName);
            if (propertyValue != null && !propertyValue.isEmpty()) {
                var cell = row.createCell(columnIndex);
                cell.setCellValue(propertyValue);
            }
        }
    }

    /**
     * Выполняет сопоставление имени атрибута и индекса колонки на основании информации на листе.
     *
     * @param sheet              Лист Excel
     * @param propertiesRowIndex Индекс строки с именами свойств
     * @return (Имя атрибута - > Индекс колонки)
     */
    private static ImmutableMap<String, Integer> createPropertyMapping(Sheet sheet, int propertiesRowIndex) {

        var resultBuilder = ImmutableMap.<String, Integer>builder();

        var header = sheet.getRow(propertiesRowIndex);

        for (int i = 0; i < header.getLastCellNum(); i++) {
            Cell cell = header.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) {
                continue;
            }

            String value = HtmlUtils.cleanAll(cell.getStringCellValue());
            if (value == null || value.isEmpty())
                continue;

            resultBuilder.put(value, i);
        }

        return resultBuilder.build();
    }

    private static String getCellValueAsPlainString(@Nullable Cell cell) {
        if (cell == null)
            return null;

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }
}
