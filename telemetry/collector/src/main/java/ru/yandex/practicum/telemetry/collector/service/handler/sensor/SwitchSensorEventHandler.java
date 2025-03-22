package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaClientProducer;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SwitchSensorEvent;

@Component
public class SwitchSensorEventHandler extends BaseSensorEventHandler<SwitchSensorAvro> {
    public SwitchSensorEventHandler(KafkaClientProducer producer) {
        super(producer);
    }

    @Override
    protected SwitchSensorAvro mapToAvro(SensorEvent event) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) event;
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
