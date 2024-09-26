package ru.d4nik.carparts.components.kiselev.service.usecase;

import jakarta.mail.Session;
import org.eclipse.angus.mail.smtp.SMTPMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.d4nik.carparts.components.kiselev.IntegrationTest;
import ru.d4nik.carparts.components.kiselev.adapters.dao.PriceListDao;
import ru.d4nik.carparts.components.kiselev.adapters.email.EmailAdapter;
import ru.d4nik.carparts.components.kiselev.domain.PriceListExcelFile;

import java.time.Instant;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.Mockito.*;

class LoadAndSendPriceListToArlanAvtoUsecaseTest extends IntegrationTest {
    @Autowired
    EmailAdapter emailAdapter;
    @Autowired
    PriceListDao priceListDao;
    @Autowired
    LoadAndSendPriceListToArlanAvtoUsecase loadAndSendPriceListToArlanAvtoUsecase;
    @Autowired
    JavaMailSender javaMailSender;
    @Captor
    private ArgumentCaptor<SMTPMessage> simpleMailMessageArgumentCaptor;

    @Test
    void savePriceList() {
        when(javaMailSender.createMimeMessage()).thenReturn(new SMTPMessage(Session.getDefaultInstance(new Properties())));

        doReturn(Optional.of(PriceListExcelFile.builder()
                .inputStream(this.getClass().getResourceAsStream("/Тестовый прайс лист.xlsx"))
                .date(Instant.now())
                .fileName("Тестовый прайс лист.xlsx")
                .build()))
                .when(emailAdapter).loadPriceListExcelFile();
        loadAndSendPriceListToArlanAvtoUsecase.run();

        verify(javaMailSender).send(simpleMailMessageArgumentCaptor.capture());
    }

}