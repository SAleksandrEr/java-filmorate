package ru.yandex.practicum.filmorate.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmController filmController;


    @BeforeEach
    void start() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        FilmService filmService = new FilmService(filmStorage, userService);
       filmController = new FilmController(filmService);
    }

    @Test
    void validateFilmNegative() {
Film film = Film.builder()
        .name("Test")
        .description("description")
        .releaseDate(LocalDate.of(1790,9,21))
        .duration(120)
        .likes(new HashSet<>())
        .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void validateFilmPositive() {
        Film film = Film.builder()
                .name("Test")
                .description("description")
                .releaseDate(LocalDate.of(1985,9,21))
                .duration(120)
                .likes(new HashSet<>())
                .build();
        filmController.createFilm(film);
    }

}