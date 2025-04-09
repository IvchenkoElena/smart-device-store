package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookedProductsDto {
    @NotNull
    private Double deliveryWeight = 0.0;
    @NotNull
    private Double deliveryVolume = 0.0;
    @NotNull
    private Boolean fragile = false;
}
