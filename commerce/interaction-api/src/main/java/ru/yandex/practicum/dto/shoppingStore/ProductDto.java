package ru.yandex.practicum.dto.shoppingStore;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID productId;
    @NotNull
    private String productName;
    @NotNull
    private String description;
    private String imageSrc;
    @NotNull
    private QuantityState quantityState;
    @NotNull
    private ProductState productState;
    private ProductCategory productCategory;
    @NotNull
    @Min(1)
    private BigDecimal price;
}
