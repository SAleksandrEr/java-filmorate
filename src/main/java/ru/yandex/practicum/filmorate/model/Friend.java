package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class Friend {

    @NonNull
    private Long friendsId;

    @NonNull
    private Long userId;

    private boolean friendshipStatus;
}
