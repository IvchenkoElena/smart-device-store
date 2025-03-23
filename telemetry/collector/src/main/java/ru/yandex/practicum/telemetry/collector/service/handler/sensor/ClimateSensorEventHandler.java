package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.telemetry.collector.kafka.KafkaClientProducer;

@Component
public class ClimateSensorEventHandler extends BaseSensorEventHandler<ClimateSensorAvro> {
    public ClimateSensorEventHandler(KafkaClientProducer producer) {
        super(producer);
    }

    @Override
    protected ClimateSensorAvro mapToAvro(SensorEventProto event) {
        ClimateSensorProto climateSensorEvent = event.getClimateSensorEvent();
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .setHumidity(climateSensorEvent.getHumidity())
                .setCo2Level(climateSensorEvent.getCo2Level())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR_EVENT;
    }
}
