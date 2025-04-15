package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.ShoppingStoreOperations;
import ru.yandex.practicum.dto.shoppindCart.ShoppingCartDto;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.QuantityState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AddressDto warehouseAddress = initAddress();
    private final ShoppingStoreOperations shoppingStoreClient;

    @Transactional
    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        log.debug("Добавляем новый товар в перечень - {}", request);
        warehouseRepository.findById(request.getProductId())
                .ifPresent(product -> {
                    log.warn("Product with ID: {} already exists", request.getProductId());
                    throw new SpecifiedProductAlreadyInWarehouseException("Product is already in warehouse");
                });
        WarehouseProduct product = warehouseRepository.save(warehouseMapper.toEntity(request));
        log.debug("Добавили товар в перечень - {}", product);
    }

    @Transactional
    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto cartDto) {
        log.info("Запрашиваем товары из корзины {}", cartDto);
        Map<UUID, Integer> products = cartDto.getProducts();
        log.info("Запрашиваем количество доступных товаров на складе {}", products.keySet());
        List<WarehouseProduct> availableProductsList = warehouseRepository.findAllById(products.keySet());
        Map<UUID, WarehouseProduct> availableProductsMap = availableProductsList.stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            UUID id = product.getKey();
            WarehouseProduct availableProduct = availableProductsMap.get(id);
            if (availableProduct == null) {
                throw new NoSpecifiedProductInWarehouseException("Такого товара нет в перечне товаров на складе:" + product.getKey().toString());
            }
            if (availableProduct.getQuantity() >= product.getValue()) {
                Double volume = bookedProductsDto.getDeliveryVolume() + (availableProduct.getWidth() * availableProduct.getHeight() * availableProduct.getDepth()) * product.getValue();
                bookedProductsDto.setDeliveryVolume(volume);
                Double weight = bookedProductsDto.getDeliveryWeight() + (availableProduct.getWeight()) * product.getValue();
                bookedProductsDto.setDeliveryWeight(weight);
                if (availableProduct.getFragile()) {
                    bookedProductsDto.setFragile(true);
                }
            } else {String message = "Количества продукта " + availableProduct.getProductId() + " недостаточно на складе. Уменьшите количество продукта до " + availableProduct.getQuantity();
                log.info(message);
                throw new ProductInShoppingCartLowQuantityInWarehouse(message);
            }
        }
        log.info("Параметры заказа: {}", bookedProductsDto);
        return bookedProductsDto;
    }

    @Transactional
    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        log.info("Запрошено принятие товара на склад {}", request);
        WarehouseProduct product = warehouseRepository.findById(request.getProductId())
                        .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Такого товара нет в перечне товаров на складе:" + request.getProductId()));
        Integer oldQuantity = product.getQuantity();
        Integer newQuantity = oldQuantity + request.getQuantity();
        product.setQuantity(newQuantity);
        warehouseRepository.save(product);
        log.info("Приняли товар на склад");

        log.info("Проверяем, есть ли товар в магазине");
        ProductDto productDto;
        try {
            productDto = shoppingStoreClient.getProduct(product.getProductId());
            QuantityState quantityState;
            if (newQuantity > 100) {
                quantityState = QuantityState.MANY;
            } else if (newQuantity > 10) {
                quantityState = QuantityState.ENOUGH;
            } else if (newQuantity > 0) {
                quantityState = QuantityState.FEW;
            } else {
                quantityState = QuantityState.ENDED;
            }
            SetProductQuantityStateRequest stateRequest = new SetProductQuantityStateRequest(product.getProductId(), quantityState);
            log.info("Обновляем количество товара в магазине");
            shoppingStoreClient.setProductQuantityState(stateRequest);
            log.info("Обновили количество товара в магазине");
        } catch (RuntimeException e) {
            log.info("Такого товара нет в магазине");
        }
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseAddress;
    }

    private AddressDto initAddress() {
        final String[] addresses = new String[]{"ADDRESS_1", "ADDRESS_2"};
        final String address = addresses[Random.from(new SecureRandom()).nextInt(0, 1)];
        return AddressDto.builder()
                .city(address)
                .street(address)
                .house(address)
                .country(address)
                .flat(address)
                .build();
    }
}
