package ru.yandex.practicum.filmorate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    private final LikesStorage likesStorage;


    @Autowired
    public FilmController(FilmService filmService, LikesStorage likesStorage) {
        this.filmService = filmService;
        this.likesStorage = likesStorage;
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
    public List<Film> findFilmsOfLikes(@RequestParam(defaultValue = "10", required = false) Long count) {
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
}
