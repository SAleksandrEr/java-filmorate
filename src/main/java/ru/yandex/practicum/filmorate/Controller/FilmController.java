package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilm() {
        return filmStorage.getAllFilm();
    }

    @GetMapping("/{id}")
    public Film findFilmsId(@PathVariable("id") Optional<Long> id) {
        if (id.isPresent()) {
            return filmService.findFilmsId(id.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get());
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public Film createLikeFilm(@PathVariable("id") Optional<Long> id,
                           @PathVariable("userId") Optional<Long> userId) {
        if (id.isPresent() & userId.isPresent()) {
            return filmService.createLikeFilm(id.get(),userId.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get() + " " + userId.get());
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeId(@PathVariable("id") Optional<Long> id,
                        @PathVariable("userId") Optional<Long> userId) {
        if (id.isPresent() & userId.isPresent()) {
            return filmService.deleteLikeId(id.get(),userId.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + id.get() + " " + userId.get());
        }
    }

    @GetMapping("/popular")
    public List<Film> findFilmsOfLikes(@RequestParam(defaultValue = "10", required = false) Optional<Integer> count) {
        if (count.isPresent() & (count.get() > 0)) {
           return filmService.findFilmsOfLikes(count.get());
        } else {
            throw new ValidationException("Введенные данные не корректны " + count.get());
        }
    }
}
