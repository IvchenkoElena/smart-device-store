package ru.yandex.practicum.handlers.event;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventHandler {
    void handle(HubEventAvro event);
    String getPayloadType();
}
