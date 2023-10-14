package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void start() {
        filmController = new FilmController();
    }

    @Test
    void validateFilmNegative() {
Film film = Film.builder()
        .name("Test")
        .description("description")
        .releaseDate(LocalDate.of(1790,9,21))
        .duration(120)
        .build();
        assertThrows(ValidationException.class, () -> filmController.validate(film));
    }

    @Test
    void validateFilmPositive() {
        Film film = Film.builder()
                .name("Test")
                .description("description")
                .releaseDate(LocalDate.of(1985,9,21))
                .duration(120)
                .build();
        filmController.validate(film);
    }

}