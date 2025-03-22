package ru.yandex.practicum.telemetry.collector.model.hub.device;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank
    private String id;

    @Override
    public HubEventType getType() {

        return HubEventType.DEVICE_REMOVED;
    }
}
