package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    Double deliveryCost(OrderDto orderDto);

    void deliveryPicked(UUID orderId);

    void deliverySuccessful(UUID orderId);

    void deliveryFailed(UUID orderId);
}
