package ru.yandex.practicum.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handlers.event.HubEventHandler;
import ru.yandex.practicum.handlers.event.HubEventHandlers;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> hubConsumer;
    private final HubEventHandlers handlers;
    @Value("${kafka.topics.hubs}")
    private String hubsTopic;
    private static final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    @Override
    public void run() {
        try {
            hubConsumer.subscribe(List.of(hubsTopic));
            log.info("Подписались на топик хабов");
            Runtime.getRuntime().addShutdownHook(new Thread(hubConsumer::wakeup));

            while (true) {

                ConsumerRecords<String, HubEventAvro> records = hubConsumer.poll(Duration.ofMillis(1000));

                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    // обрабатываем очередную запись
                    handleRecord(record);
                    // фиксируем оффсеты обработанных записей, если нужно
                    manageOffsets(record, count, hubConsumer);
                    count++;
                }
                // фиксируем максимальный оффсет обработанных записей
                hubConsumer.commitAsync();
                log.info("Смещения зафиксированы - хаб");
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", hubsTopic);
        } finally {
            try {
                hubConsumer.commitSync();
            } finally {
                hubConsumer.close();
            }
        }
    }

    private static void manageOffsets(ConsumerRecord<String, HubEventAvro> record, int count, KafkaConsumer<String, HubEventAvro> consumer) {
        // обновляем текущий оффсет для топика-партиции
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if(count % 10 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if(exception != null) {
                    log.warn("Ошибка во время фиксации оффсетов: {}", offsets, exception);
                }
            });
        }
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record) throws InterruptedException {
        log.info("топик = {}, партиция = {}, смещение = {}, значение: {}\n",
                record.topic(), record.partition(), record.offset(), record.value());
        Map<String, HubEventHandler> handlerMap = handlers.getHandlers();
        HubEventAvro event = record.value();
        String payloadName = event.getPayload().getClass().getSimpleName();
        log.info("Получили сообщение хаба типа: {}", payloadName);

        if (handlerMap.containsKey(payloadName)) {
            handlerMap.get(payloadName).handle(event);
        } else {
            throw new IllegalArgumentException("Не могу найти обработчик для события " + event);
        }
    }
}