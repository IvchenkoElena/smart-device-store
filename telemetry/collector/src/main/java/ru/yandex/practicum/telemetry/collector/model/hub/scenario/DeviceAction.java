package ru.yandex.practicum.telemetry.collector.model.hub.scenario;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {
    @NotBlank
    private String sensorId;
    private ActionType type;
    private int value;
}
