package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class OrderDto {
    @NotNull
    private UUID orderId;
    private UUID shoppingCartId;
    @NotNull
    private Map<UUID, Integer> products;
    private UUID paymentId;
    private UUID deliveryId;
    private OrderState orderState;
    private Double deliveryWeight;
    private Double deliveryVolume;
    private Boolean fragile;
    private Double totalPrice;
    private Double deliveryPrice;
    private Double productPrice;
}
