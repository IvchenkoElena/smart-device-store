package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.shoppingStore.ProductCategory;
import ru.yandex.practicum.dto.shoppingStore.ProductDto;
import ru.yandex.practicum.dto.shoppingStore.ProductState;
import ru.yandex.practicum.dto.shoppingStore.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        log.debug("Запрашиваем товары с категорией - {} и пагинацией - {}", category, pageable);
        List<Product> products = productRepository.findAllByProductCategory(category, pageable).getContent();
        log.debug("Получили из DB список товаров размером {}", products.size());
        return products.stream()
                .map(productMapper::toProductDto)
                .toList();
    }

    @Transactional
    @Override
    public ProductDto addProduct(ProductDto productDto) {
        log.debug("Сохраняем новый товар в DB - {}", productDto);
        Product product = productMapper.toProduct(productDto);
        productRepository.save(product);
        log.debug("Сохранили товар в DB - {}", product);
        return productMapper.toProductDto(product);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        log.debug("Обновляем товар в DB - {}", productDto);
        if (!productRepository.existsById(productDto.getProductId())) {
            throw new ProductNotFoundException("Продукта с id " + productDto.getProductId() + " не существует");
        }
        Product product = productRepository.save(productMapper.toProduct(productDto));
        log.debug("Обновили товар в DB - {}", product);
        return productMapper.toProductDto(product);
    }

    @Transactional
    @Override
    public boolean updateQuantityState(SetProductQuantityStateRequest request) {
        log.debug("Обновляем количество товара в DB - {}", request);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id " + request.getProductId() + " не существует"));
        product.setQuantityState(request.getQuantityState());
        productRepository.save(product);
        log.debug("Обновили количество товара в DB - {}", product);
        return true;
    }

    @Transactional
    @Override
    public boolean removeProduct(UUID productId) {
        log.debug("Деактивируем товар в DB c ID - {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id " + productId + " не существует"));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        log.debug("Деактивировали товар в DB - {}", product);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDto getProductById(UUID productId) {
        log.debug("Запрашиваем товар с ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукта с id " + productId + " не существует"));
        log.debug("Получили из DB товар {}", product);
        return productMapper.toProductDto(product);
    }
}
