package ru.yandex.practicum.dto.shoppingStore;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
