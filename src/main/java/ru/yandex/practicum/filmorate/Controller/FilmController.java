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

    @DeleteMapping("/{id}") //удаление фильма по id
    public void filmDeleteById(@PathVariable("id") Long filmId) {
        filmService.filmDeleteById(filmId);
    }

    @GetMapping("/common")
    public List<Film> getPopularFilmsForFriends(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }


    @GetMapping("/director/{id}")
    public List<Film> getFilmByDirectorId(@PathVariable("id") Long id, @RequestParam String sortBy) {
        return filmService.getFilmsByDirectorId(id, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchNameFilmsAndDirectors(@RequestParam(value = "query", required = false) String query,
                                                  @RequestParam(value = "by", defaultValue = "title", required = false) List<String> by) {
        if ((by.size() > 2)) {
            throw new ValidationException("Введенные данные не корректны " + by);
          }
        return filmService.searchNameFilmsAndDirectors(query, by);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Long count,
                                      @RequestParam(required = false) Long genreId,
                                      @RequestParam(required = false) Long year) {
        return filmService.getPopularFilms(count, genreId, year);
    }
}
