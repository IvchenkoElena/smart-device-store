package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/shopping-store")
public class ShoppingStoreController implements ShoppingStoreClient {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public List<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.info("GET /api/v1/shopping-store - Получение списка товаров: category={}, pageable={}",
                category, pageable);
        List<ProductDto> response = shoppingStoreService.getProducts(category, pageable);
        log.info("Возвращаем список товаров размером: {}", response.size());
        log.debug("Возвращаем товары: {}", response);
        return response;
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        log.info("POST /api/v1/shopping-store - Добавление товара: {}", productDto);
        ProductDto response = shoppingStoreService.addProduct(productDto);
        log.info("Возвращаем товар: {}", response);
        return response;
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store - Обновление товара: {}", productDto);
        ProductDto response = shoppingStoreService.updateProduct(productDto);
        log.info("Возвращаем товар: {}", response);
        return response;
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        log.info("PUT /api/v1/shopping-store/removeProductFromStore - Удаление товара: {}", productId);
        boolean response = shoppingStoreService.removeProduct(productId);
        log.info("Удалили товар: {}", response);
        return response;
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        log.info("PUT /api/v1/shopping-store/quantityState - Обновление количества: {}", request);
        boolean response = shoppingStoreService.updateQuantityState(request);
        log.info("Обновили количество товаров: {}", response);
        return response;
    }

    @Override
    public ProductDto getProduct(UUID productId) {
        log.info("GET /api/v1/shopping-store/{} - Получение товара", productId);
        ProductDto response = shoppingStoreService.getProductById(productId);
        log.info("Возвращаем товар: {}", response);
        return response;
    }
}
