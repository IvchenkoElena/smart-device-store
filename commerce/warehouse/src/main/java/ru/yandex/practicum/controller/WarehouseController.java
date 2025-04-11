package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.api.WarehouseOperations;
import ru.yandex.practicum.dto.shoppindCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.info("PUT /api/v1/warehouse - Добавить новый товар на склад {}", request);
        warehouseService.newProductInWarehouse(request);
        log.info("Товар добавлен на склад", request);
    }

    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        log.info("POST /api/v1/warehouse/check - Проверка количества товаров на складе: {}", cartDto);
        BookedProductsDto response = warehouseService.checkProductQuantityEnoughForShoppingCart(cartDto);
        log.info("Товары зарезервированы: {}", response);
        return response;
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("POST /api/v1/warehouse/add - Принять товар на склад: {}", request);
        warehouseService.addProductToWarehouse(request);
        log.info("Товар принят на склад: {}", request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        log.info("GET /api/v1/warehouse/address - Предоставить адрес склада для расчёта доставки");
        AddressDto response = warehouseService.getWarehouseAddress();
        log.info("Адрес предоставлен: {}", response);
        return response;
    }
}
