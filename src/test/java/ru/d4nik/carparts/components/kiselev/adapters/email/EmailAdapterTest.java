package ru.d4nik.carparts.components.kiselev.adapters.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.d4nik.carparts.components.kiselev.IntegrationTest;

class EmailAdapterTest extends IntegrationTest {

    @Autowired
    EmailAdapter emailAdapter;

    @Test
    void loadPriceListFile() {
        emailAdapter.loadPriceListExcelFile();
    }
}