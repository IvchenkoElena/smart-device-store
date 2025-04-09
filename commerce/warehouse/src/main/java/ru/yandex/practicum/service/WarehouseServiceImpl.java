package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppindCart.ShoppingCartDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AddressDto warehouseAddress = initAddress();

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
        List<WarehouseProduct> availableProductsList = warehouseRepository.findAllByProductIdIn(products.keySet());
        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            UUID id = product.getKey();
            WarehouseProduct availableProduct = availableProductsList.stream().filter(p -> p.getProductId().equals(id)).findFirst()
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Такого товара нет в перечне товаров на складе:" + product.getKey().toString()));
            if (availableProduct.getQuantity() >= product.getValue()) {
                bookedProductsDto.setDeliveryVolume(bookedProductsDto.getDeliveryVolume() + (availableProduct.getWidth() * availableProduct.getHeight() * availableProduct.getDepth()) * product.getValue());
                bookedProductsDto.setDeliveryWeight(bookedProductsDto.getDeliveryWeight() + (availableProduct.getWeight()) * product.getValue());
                if (availableProduct.getFragile()) {
                    bookedProductsDto.setFragile(true);
                }
            } else
            {
                String message = "Количества продукта " + availableProduct.getProductId() + " недостаточно на складе. Уменьшите количество продукта до " + availableProduct.getQuantity();
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
        product.setQuantity(oldQuantity + request.getQuantity());
        warehouseRepository.save(product);
        log.info("Приняли товар на склад");

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
