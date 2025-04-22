package ru.yandex.practicum.api;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.shoppindCart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseOperations {

    @PutMapping
    void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto cartDto) throws FeignException;

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
