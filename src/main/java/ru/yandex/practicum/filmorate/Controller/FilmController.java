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
    public Film findFilmsId(@PathVariable("id") Long id) {
            return filmService.findFilmsId(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film createLikeFilm(@PathVariable("id") Long id,
                           @PathVariable("userId") Long userId) {
            return filmService.createLikeFilm(id,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLikeId(@PathVariable("id") Long id,
                        @PathVariable("userId") Long userId) {
            return filmService.deleteLikeId(id,userId);
    }

    @GetMapping("/popular")
    public List<Film> findFilmsOfLikes(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count > 0) {
           return filmService.findFilmsOfLikes(count);
        } else {
            throw new ValidationException("Введенные данные не корректны " + count);
        }
    }

    @DeleteMapping("/{id}") //удаление фильма по id
    public void filmDeleteById(@PathVariable("id") Long filmId) {
        filmService.filmDeleteById(filmId);
    }
}
