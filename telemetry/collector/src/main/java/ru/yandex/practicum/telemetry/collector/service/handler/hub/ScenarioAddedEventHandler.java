package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.scenario.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.hub.scenario.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.scenario.ScenarioCondition;

public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
//    protected ScenarioAddedEventHandler(Producer<String, SpecificRecordBase> producer) {
//        super(producer);
//    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(scenarioAddedEvent.getConditions().stream()
                        .map(this::mapToConditionAvro)
                        .toList())
                .setActions(scenarioAddedEvent.getActions().stream()
                        .map(this::mapToActionAvro)
                        .toList())
                .build();
    }

    private ScenarioConditionAvro mapToConditionAvro(ScenarioCondition scenarioCondition) {
        ConditionTypeAvro conditionTypeAvro = switch (scenarioCondition.getType()) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
        };
        ConditionOperationAvro conditionOperationAvro = switch (scenarioCondition.getOperation()) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
        };
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setOperation(conditionOperationAvro)
                .setType(conditionTypeAvro)
                .setValue(scenarioCondition.getValue())
                .build();
    }

    private DeviceActionAvro mapToActionAvro(DeviceAction deviceAction) {
        ActionTypeAvro actionTypeAvro = switch (deviceAction.getType()) {
            case INVERSE -> ActionTypeAvro.INVERSE;
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
        };
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(actionTypeAvro)
                .setValue(deviceAction.getValue())
                .build();
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }
}