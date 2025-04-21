package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.api.WarehouseOperations;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseOperations warehouseClient;

    @Transactional(readOnly = true)
    @Override
    public List<OrderDto> getClientOrders(String username) {
        validateUsername(username);
        log.info("Запрашиваем список всех заказов пользователя {}", username);
        List<Order> orders = orderRepository.findAllByUsername(username);
        log.debug("Получили из DB список заказов размером {}", orders.size());
        return orders.stream().
                map(orderMapper::toOrderDto).
                toList();
    }

    private void validateUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException(username);
        }
    }

    @Transactional
    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Создаем новый заказ: shoppingCartId {}, products {}", request.getShoppingCart().getCartId(), request.getShoppingCart().getProducts());
        BookedProductsDto bookedProductsDto = warehouseClient.checkProductQuantityEnoughForShoppingCart(request.getShoppingCart());
        log.info("Проверили наличие товаров на складе, параметры заказа: {}", bookedProductsDto);
        // пробросить ошибку из feign клиента, если товара нет на складе (как?)
        // NoSpecifiedProductInWarehouseException 400
        // только ее? а если недостаточно товара?
        Order newOrder = orderMapper.toOrder(request, bookedProductsDto);
        newOrder = orderRepository.save(newOrder);
        log.info("Сохранили новый заказ в БД: {}", newOrder);
        return orderMapper.toOrderDto(newOrder);
    }

    @Transactional
    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        log.info("Создан запрос на возврат заказа OrderId: {}, products: {}", request.getOrderId(), request.getProducts());
        Order orderToReturn = orderRepository.findById(request.getOrderId())
                        .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + request.getOrderId()));
        Map<UUID, Integer> productsToReturn = request.getProducts();
        Set<UUID> ids = productsToReturn.keySet();
        for (UUID id : ids) {
            AddProductToWarehouseRequest addProductToWarehouseRequest = new AddProductToWarehouseRequest(id, productsToReturn.get(id));
            warehouseClient.addProductToWarehouse(addProductToWarehouseRequest);
        }
        log.info("Вернули на склад товары из заказа: OrderId {}, products {}", request.getOrderId(), request.getProducts());
        orderToReturn.setOrderState(OrderState.PRODUCT_RETURNED);
        log.info("Изменили статус заказа на PRODUCT_RETURNED: OrderId {}", request.getOrderId());
        orderToReturn = orderRepository.save(orderToReturn);
        log.info("Сохранили изменения в БД: {}", orderToReturn);
        return orderMapper.toOrderDto(orderToReturn);
    }

    @Transactional
    @Override
    public OrderDto payment(UUID orderId) {
        log.info("Обрабатываем успешный платеж по заказу OrderId: {}", orderId);
        Order orderToPay = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        // когда мы используем статус ON_PAYMENT???
//        orderToPay.setOrderState(OrderState.ON_PAYMENT);
//        log.info("Изменили статус заказа на ON_PAYMENT: OrderId {}", orderId);
        //создать payment, внести ее ID
        //log.info("Ва: OrderId {}, products {}", request.getOrderId(), request.getProducts());
        orderToPay.setOrderState(OrderState.PAID);
        log.info("Изменили статус заказа на PAID: OrderId {}", orderId);
        orderToPay = orderRepository.save(orderToPay);
        log.info("Сохранили изменения в БД: {}", orderToPay);
        return orderMapper.toOrderDto(orderToPay);
    }

    @Transactional
    @Override
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Обрабатываем ошибку оплаты заказа OrderId: {}", orderId);
        Order orderToPay = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        //создать payment, внести ее ID
        //log.info("Ва: OrderId {}, products {}", request.getOrderId(), request.getProducts());
        orderToPay.setOrderState(OrderState.PAYMENT_FAILED);
        log.info("Изменили статус заказа на PAYMENT_FAILED: OrderId {}", orderId);
        orderToPay = orderRepository.save(orderToPay);
        log.info("Сохранили изменения в БД: {}", orderToPay);
        return orderMapper.toOrderDto(orderToPay);
    }

    @Transactional
    @Override
    public OrderDto delivery(UUID orderId) {
        log.info("Обрабатываем успешную доставку по заказу OrderId: {}", orderId);
        Order orderToDeliver = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        orderToDeliver.setOrderState(OrderState.DELIVERED);
        log.info("Изменили статус заказа на DELIVERED: OrderId {}", orderId);
        orderToDeliver = orderRepository.save(orderToDeliver);
        log.info("Сохранили изменения в БД: {}", orderToDeliver);
        return orderMapper.toOrderDto(orderToDeliver);
    }

    @Transactional
    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Обрабатываем ошибку доставки заказа OrderId: {}", orderId);
        Order orderToDeliver = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        orderToDeliver.setOrderState(OrderState.DELIVERED);
        log.info("Изменили статус заказа на DELIVERED: OrderId {}", orderId);
        orderToDeliver = orderRepository.save(orderToDeliver);
        log.info("Сохранили изменения в БД: {}", orderToDeliver);
        return orderMapper.toOrderDto(orderToDeliver);
    }

    @Transactional
    @Override
    public OrderDto complete(UUID orderId) {
        log.info("Обрабатываем завершение заказа OrderId: {}", orderId);
        Order orderToComplete = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        orderToComplete.setOrderState(OrderState.COMPLETED);
        log.info("Изменили статус заказа на COMPLETED: OrderId {}", orderId);
        orderToComplete = orderRepository.save(orderToComplete);
        log.info("Сохранили изменения в БД: {}", orderToComplete);
        return orderMapper.toOrderDto(orderToComplete);
    }

    @Transactional
    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Обрабатываем вычисление общей стоимости заказа OrderId: {}", orderId);
        Order orderToCalculate = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));

        //вычислить и задать поле

        orderToCalculate = orderRepository.save(orderToCalculate);
        log.info("Сохранили изменения в БД: {}", orderToCalculate);
        return orderMapper.toOrderDto(orderToCalculate);
    }

    @Transactional
    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Обрабатываем вычисление стоимости доставки заказа OrderId: {}", orderId);
        Order orderToCalculate = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));

        //вычислить и задать поле

        orderToCalculate = orderRepository.save(orderToCalculate);
        log.info("Сохранили изменения в БД: {}", orderToCalculate);
        return orderMapper.toOrderDto(orderToCalculate);
    }

    @Transactional
    @Override
    public OrderDto assembly(UUID orderId) {
        log.info("Обрабатываем успешную сборку заказа OrderId: {}", orderId);
        Order orderToAssembly = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        orderToAssembly.setOrderState(OrderState.ASSEMBLED);
        log.info("Изменили статус заказа на ASSEMBLED: OrderId {}", orderId);
        orderToAssembly = orderRepository.save(orderToAssembly);
        log.info("Сохранили изменения в БД: {}", orderToAssembly);
        return orderMapper.toOrderDto(orderToAssembly);
    }

    @Transactional
    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Обрабатываем ошибку сборки по заказу OrderId: {}", orderId);
        Order orderToAssembly = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Такого заказа нет в базе: " + orderId));
        orderToAssembly.setOrderState(OrderState.ASSEMBLY_FAILED);
        log.info("Изменили статус заказа на ASSEMBLY_FAILED: OrderId {}", orderId);
        orderToAssembly = orderRepository.save(orderToAssembly);
        log.info("Сохранили изменения в БД: {}", orderToAssembly);
        return orderMapper.toOrderDto(orderToAssembly);
    }
}
