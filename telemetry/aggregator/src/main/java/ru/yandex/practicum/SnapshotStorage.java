package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SnapshotStorage {
    private final Map<String, SensorsSnapshotAvro> snapshotsMap = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
//        Проверяем, есть ли снапшот для event.getHubId()
//        Если снапшот есть, то достаём его
//        Если нет, то созадём новый
        SensorsSnapshotAvro snapshot;
        if (!snapshotsMap.containsKey(event.getHubId())) {
            snapshot = new SensorsSnapshotAvro();
            snapshot.setHubId(event.getHubId());
            snapshot.setSensorsState(new HashMap<>());
        } else {
             snapshot = snapshotsMap.get(event.getHubId());
            //        Проверяем, есть ли в снапшоте данные для event.getId()
//        Если данные есть, то достаём их в переменную oldState
            if (snapshot.getSensorsState().containsKey(event.getId())) {
                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
//        Проверка, если oldState.getTimestamp() произошёл позже, чем
//        event.getTimestamp() или oldState.getData() равен
//        event.getPayload(), то ничего обновлять не нужно, выходим из метода
//        вернув Optional.empty()
                if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                    log.debug("Обновление снапшота не требуется для hubId={}, sensorId={}, timestamp={}",
                            event.getHubId(), event.getId(), event.getTimestamp());
                    return Optional.empty();
                }
            }
        }
        // если дошли до сюда, значит, пришли новые данные и
        // снапшот нужно обновить
        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());

        snapshot.getSensorsState().put(event.getId(), newState);
        snapshot.setTimestamp(event.getTimestamp());
        log.debug("Состояние обновлено для sensorId={}, новое состояние={}",
                event.getId(), newState);

        return Optional.of(snapshot);
//        Создаём экземпляр SensorStateAvro на основе данных события
//        Добавляем полученный экземпляр в снапшот
//        Обновляем таймстемп снапшота таймстемпом из события
//        Возвращаем снапшот - Optional.of(snapshot)
    }
}
