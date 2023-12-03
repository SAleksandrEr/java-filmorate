package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class Event {

    Long eventId;

    Long timestamp;

    Long userId;

    EventType eventType;

    EventOperation operation;

    Long entityId;
}
