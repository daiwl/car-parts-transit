package ru.d4nik.carparts.components.kiselev.ports.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.d4nik.carparts.components.kiselev.api.MaintenanceFacade;

@Tag(name = "API для поддержки сервиса")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
@RequestMapping("/kiselev/maintenance")
public class MaintenanceController {
    MaintenanceFacade maintenanceFacade;

    @Operation(summary = "Переотправка прайс листа в АрланАвто")
    @PostMapping(value = "/resend-to-arlan-avto")
    public void resendToArlanAvto() {
        maintenanceFacade.resendToArlanAvto();
    }
}
