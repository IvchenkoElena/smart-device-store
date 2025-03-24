package ru.yandex.practicum.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        Optional<Scenario> scenarioOpt = scenarioRepository.findByHubIdAndName(event.getHubId(),
                scenarioAddedEvent.getName());

        if (scenarioOpt.isEmpty()) {
            Scenario scenario = scenarioRepository.save(mapToScenario(event));
            if (checkSensorsInScenarioConditions(scenarioAddedEvent, event.getHubId())) {
                conditionRepository.saveAll(mapToCondition(scenarioAddedEvent, scenario));
            }
            if (checkSensorsInScenarioActions(scenarioAddedEvent, event.getHubId())) {
                actionRepository.saveAll(mapToAction(scenarioAddedEvent, scenario));
            }
        } else {
            Scenario scenario = scenarioOpt.get();

            if (checkSensorsInScenarioConditions(scenarioAddedEvent, event.getHubId())) {
                conditionRepository.saveAll(mapToCondition(scenarioAddedEvent, scenario));
            }

            if (checkSensorsInScenarioActions(scenarioAddedEvent, event.getHubId())) {
                actionRepository.saveAll(mapToAction(scenarioAddedEvent, scenario));
            }
        }
    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    private Scenario mapToScenario(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        return Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(event.getHubId())
                .build();
    }

    private Set<Condition> mapToCondition(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario) {
        return scenarioAddedEvent.getConditions().stream()
                .map(c -> Condition.builder()
                        .sensor(sensorRepository.findById(c.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(c.getType())
                        .operation(c.getOperation())
                        .value(setValue(c.getValue()))
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<Action> mapToAction(ScenarioAddedEventAvro scenarioAddedEvent, Scenario scenario) {
        log.info("Обрабатываем список действий {}", scenarioAddedEvent.getActions());
        return scenarioAddedEvent.getActions().stream()
                .map(action -> Action.builder()
                        .sensor(sensorRepository.findById(action.getSensorId()).orElseThrow())
                        .scenario(scenario)
                        .type(action.getType())
                        .value(action.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    private Integer setValue(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return (Boolean) value ? 1 : 0;
        }
    }

    private boolean checkSensorsInScenarioConditions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getConditions().stream()
                .map(ScenarioConditionAvro::getSensorId)
                .toList(), hubId);
    }

    private boolean checkSensorsInScenarioActions(ScenarioAddedEventAvro scenarioAddedEvent, String hubId) {
        return sensorRepository.existsByIdInAndHubId(scenarioAddedEvent.getActions().stream()
                .map(DeviceActionAvro::getSensorId)
                .toList(), hubId);
    }
}
