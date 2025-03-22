package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaClientProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.device.DeviceAddedEvent;

@Component
public class DeviceAddedEventHandler extends BaseHubEventHandler<DeviceAddedEventAvro> {
    public DeviceAddedEventHandler(KafkaClientProducer producer) {
        super(producer);
    }

    @Override
    protected DeviceAddedEventAvro mapToAvro(HubEvent event) {
        DeviceAddedEvent deviceAddedEvent = (DeviceAddedEvent) event;

        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(DeviceTypeAvro.valueOf(deviceAddedEvent.getDeviceType().name()))
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }
}