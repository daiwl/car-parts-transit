package ru.d4nik.carparts.components.kiselev.service.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.d4nik.carparts.components.kiselev.adapters.arlanavto.ArlanAvtoAdapter;
import ru.d4nik.carparts.components.kiselev.adapters.dao.PriceListDao;
import ru.d4nik.carparts.components.kiselev.adapters.email.EmailAdapter;
import ru.d4nik.carparts.components.kiselev.adapters.excel.PriceListAdapter;
import ru.d4nik.carparts.components.kiselev.domain.PriceList;
import ru.d4nik.carparts.components.kiselev.domain.PriceListExcelFile;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadAndSendPriceListToArlanAvtoUsecase {
    final ArlanAvtoAdapter arlanAvtoAdapter;
    private final PriceListAdapter priceListAdapter;
    private final EmailAdapter emailAdapter;
    private final PriceListDao priceListDao;

    public void run() {
        Optional<PriceListExcelFile> file = emailAdapter.loadPriceListExcelFile();
        if (file.isEmpty()) {
            log.warn("Нет новых писем с прайс листом");
            return;
        }
        log.info("Файл с прайс листом загружен. Дата: {}", file.get().date());
        var products = priceListAdapter.parse(file.get());
        log.info("Файл с прайс листом обработан. Товаров: {}", products.size());
        var priceList = PriceList.builder()
                .date(file.get().date())
                .products(products)
                .build();
        priceListDao.save(priceList);
        log.info("Прайс лист сохранен");
        arlanAvtoAdapter.sendPriceList(priceList);
        log.info("Отправлен прайс лист в Арлан Авто. Дата: {}. Товаров: {}", priceList.date(), priceList.products().size());
    }
}
