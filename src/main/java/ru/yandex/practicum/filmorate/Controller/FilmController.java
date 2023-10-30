package ru.yandex.practicum.filmorate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilms(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilms(film);
    }

    @GetMapping
    public List<Film> getAllFilm() {
        return filmService.getAllFilms();
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
