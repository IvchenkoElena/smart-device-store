package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.shoppingStore.PageableDto;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {

    List<ProductDto> getProducts(ProductCategory category, PageableDto pageable);

    ProductDto addProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean updateQuantityState(SetProductQuantityStateRequest request);

    boolean removeProduct(UUID productId);

    ProductDto getProductById(UUID productId);
}
