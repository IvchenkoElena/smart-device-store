package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.util.UUID;

@Data
public class DeliveryDto {
    private UUID deliveryId;
    @NotNull
    private AddressDto fromAddress;
    @NotNull
    private AddressDto toAddress;
    @NotNull
    private UUID orderId;
    private DeliveryState deliveryState;
}
