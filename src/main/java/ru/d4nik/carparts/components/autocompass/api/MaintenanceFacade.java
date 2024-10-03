package ru.d4nik.carparts.components.autocompass.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.d4nik.carparts.components.autocompass.service.usecase.LoadAndSendPriceListToArlanAvtoUsecase;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaintenanceFacade {
    LoadAndSendPriceListToArlanAvtoUsecase loadAndSendPriceListToArlanAvtoUsecase;
    public void resendToArlanAvto() {
        loadAndSendPriceListToArlanAvtoUsecase.run();
    }
}
