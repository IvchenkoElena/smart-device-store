package ru.yandex.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.api.ShoppingCartOperations;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient extends ShoppingCartOperations {
}
