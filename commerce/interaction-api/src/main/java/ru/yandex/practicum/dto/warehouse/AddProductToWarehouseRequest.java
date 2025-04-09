package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddProductToWarehouseRequest {
    @NotNull
    private UUID productId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
