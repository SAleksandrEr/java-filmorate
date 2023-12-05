package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class Likes {

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;
}
