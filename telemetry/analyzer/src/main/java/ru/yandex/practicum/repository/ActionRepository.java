package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;

import java.util.List;

public interface ActionRepository extends JpaRepository<Action, Long> {
    void deleteByScenario(Scenario scenario);
    List<Action> findAllByScenario(Scenario scenario);
}