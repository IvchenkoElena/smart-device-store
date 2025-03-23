package ru.yandex.practicum;


import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
public class EventDataProducer {
    private final SensorConfig sensorConfig;

    private final Map<String, Integer> lastValues = new HashMap<>();

    @GrpcClient("collector")
    private CollectorControllerGrpc.CollectorControllerBlockingStub collectorStub;

    @Autowired
    public EventDataProducer(SensorConfig sensorConfig) {
        this.sensorConfig = sensorConfig;
    }

    // Генерация и отправка события от случайного датчика
    @Scheduled(fixedRate = 1000)
    public void generateAndSendRandomEvent() {
        // Случайно выбираем тип датчика (0 - Climate, 1 - Light, 2 - Motion, 3 - Switch, 4 - Temperature)
        int sensorType = ThreadLocalRandom.current().nextInt(5);

        switch (sensorType) {
            case 0 -> {
                SensorConfig.ClimateSensor sensor = getRandomSensor(sensorConfig.getClimateSensors());
                log.debug("Создаем событие от климатического датчика на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createClimateSensorEvent(sensor));
                }
            }
            case 1 -> {
                SensorConfig.LightSensor sensor = getRandomSensor(sensorConfig.getLightSensors());
                log.debug("Создаем событие от датчика освещенности на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createLightSensorEvent(sensor));
                }
            }
            case 2 -> {
                SensorConfig.MotionSensor sensor = getRandomSensor(sensorConfig.getMotionSensors());
                log.debug("Создаем событие от датчика движения на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createMotionSensorEvent(sensor));
                }
            }
            case 3 -> {
                SensorConfig.SwitchSensor sensor = getRandomSensor(sensorConfig.getSwitchSensors());
                log.debug("Создаем событие от датчика выключателя на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createSwitchSensorEvent(sensor));
                }
            }
            case 4 -> {
                SensorConfig.TemperatureSensor sensor = getRandomSensor(sensorConfig.getTemperatureSensors());
                log.debug("Создаем событие от датчика температуры на основе настройки {}", sensor);
                if (sensor != null) {
                    sendEvent(createTemperatureSensorEvent(sensor));
                }
            }
            default -> throw new IllegalStateException("Сгенерировали датчик неизвестного типа: " + sensorType);
        }
    }

    private void sendEvent(SensorEventProto event) {
        log.info("Отправляем данные: {}", event.getAllFields());
        collectorStub.collectSensorEvent(event);
    }

    // Генерация значения с небольшим отклонением от предыдущего
    private int generateValueWithDeviation(SensorConfig.Range range, String sensorId) {
        int lastValue = lastValues.getOrDefault(sensorId, range.getMinValue());
        int deviation = ThreadLocalRandom.current().nextInt(-1, 2); // Отклонение от -1 до 1
        int newValue = lastValue + deviation;

        // Ограничиваем новое значение диапазоном датчика
        newValue = Math.min(range.getMaxValue(), newValue);
        newValue = Math.max(range.getMinValue(), newValue);
        lastValues.put(sensorId, newValue);
        return newValue;
    }

    // Выбор случайного датчика из списка датчиков одного типа
    private <T> T getRandomSensor(List<T> sensors) {
        if (sensors.isEmpty()) {
            return null;
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(sensors.size());
        return sensors.get(randomIndex);
    }

    private SensorEventProto createTemperatureSensorEvent(SensorConfig.TemperatureSensor sensor) {
        int temperatureCelsius = generateValueWithDeviation(sensor.getTemperature(), sensor.getId());
        int temperatureFahrenheit = (int) (temperatureCelsius * 1.8 + 32);
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setTemperatureSensorEvent(
                        TemperatureSensorProto.newBuilder()
                                .setTemperatureC(temperatureCelsius)
                                .setTemperatureF(temperatureFahrenheit)
                                .build()
                )
                .build();
    }

    private SensorEventProto createLightSensorEvent(SensorConfig.LightSensor sensor) {
        int luminosity = generateValueWithDeviation(sensor.getLuminosity(), sensor.getId());
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setLightSensorEvent(
                        LightSensorProto.newBuilder()
                                .setLuminosity(luminosity)
                                .build()
                )
                .build();
    }

    private SensorEventProto createMotionSensorEvent(SensorConfig.MotionSensor sensor) {
        boolean isMotion = ThreadLocalRandom.current().nextBoolean();
        int linkQuality = generateValueWithDeviation(sensor.getLinkQuality(), sensor.getId() + "-linkQuality");
        int voltage = generateValueWithDeviation(sensor.getVoltage(), sensor.getId() + "-voltage");
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setMotionSensorEvent(
                        MotionSensorProto.newBuilder()
                                .setMotion(isMotion)
                                .setLinkQuality(linkQuality)
                                .setVoltage(voltage)
                                .build()
                )
                .build();
    }

    private SensorEventProto createSwitchSensorEvent(SensorConfig.SwitchSensor sensor) {
        boolean state = ThreadLocalRandom.current().nextBoolean();
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setSwitchSensorEvent(
                        SwitchSensorProto.newBuilder()
                                .setState(state)
                                .build()
                )
                .build();
    }

    private SensorEventProto createClimateSensorEvent(SensorConfig.ClimateSensor sensor) {
        int temperature = generateValueWithDeviation(sensor.getTemperature(), sensor.getId() + "-temperature");
        int humidity = generateValueWithDeviation(sensor.getHumidity(), sensor.getId() + "-humidity");
        int co2Level = generateValueWithDeviation(sensor.getCo2Level(), sensor.getId() + "-co2");
        Instant ts = Instant.now();

        return SensorEventProto.newBuilder()
                .setId(sensor.getId())
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(ts.getEpochSecond())
                        .setNanos(ts.getNano())
                ).setClimateSensorEvent(
                        ClimateSensorProto.newBuilder()
                                .setTemperatureC(temperature)
                                .setHumidity(humidity)
                                .setCo2Level(co2Level)
                                .build()
                )
                .build();
    }
}