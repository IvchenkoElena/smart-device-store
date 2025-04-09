package ru.yandex.practicum.dto.shoppindCart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    @NotNull
    private UUID cartId;
    @NotNull
    private Map<UUID, Integer> products;
}
