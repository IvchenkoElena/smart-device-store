package ru.yandex.practicum;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties(prefix = "sensor")
@Data
public class SensorConfig {
    private List<MotionSensor> motionSensors;
    private List<SwitchSensor> switchSensors;
    private List<TemperatureSensor> temperatureSensors;
    private List<LightSensor> lightSensors;
    private List<ClimateSensor> climateSensors;

    @Data
    public static class MotionSensor {
        private String id;
        private Range linkQuality;
        private Range voltage;
    }

    @Data
    public static class SwitchSensor {
        private String id;
    }

    @Data
    public static class TemperatureSensor {
        private String id;
        private Range temperature;
    }

    @Data
    public static class LightSensor {
        private String id;
        private Range luminosity;
    }

    @Data
    public static class ClimateSensor {
        private String id;
        private Range temperature;
        private Range humidity;
        private Range co2Level;
    }

    @Data
    public static class Range {
        private int minValue;
        private int maxValue;
    }
}