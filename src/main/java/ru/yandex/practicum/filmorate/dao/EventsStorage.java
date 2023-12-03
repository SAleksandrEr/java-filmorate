package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;

import java.util.List;

public interface EventsStorage {
    List<Event> findUserIdEvents(Long id);

    Event createUserIdEvents(Long entityId, Long userId, EventType eventType, EventOperation operation);

    Event getUserIdEvent(Long entityId);
}
