package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    @NotNull
    private UUID productId;
    private Boolean fragile;
    @NotNull
    private DimensionDto dimension;
    @NotNull
    @Min(1)
    private Double weight;
}
