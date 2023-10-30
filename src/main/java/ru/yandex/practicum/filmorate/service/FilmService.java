package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);

    private final FilmStorage filmStorage;

    private final UserService userService;

   @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film createFilms(Film film) {
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilms(Film film) {
           validate(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilm();
    }

    public Film findFilmsId(Long id) {
        Film film = filmStorage.getFilmsId(id);
        log.info("The film was get of ID {} ", id);
        return film;
    }

    public Film createLikeFilm(Long id, Long userId) {
        userService.findUsersId(userId);
        Film film = filmStorage.getFilmsId(id);
        film.setLikes(userId);
        log.info("The user liked the movie {} filme - ", film);
        return film;
    }

    public Film deleteLikeId(Long id, Long userId) {
        userService.findUsersId(userId);
        Film film = filmStorage.getFilmsId(id);
        film.removeLikeId(userId);
        log.info("The user deleted the like FilmID - {}", id);
        return film;
    }

    public List<Film> findFilmsOfLikes(Integer count) {
        log.info("Returns a list of the first count movies by number of likes {}", count);
        return filmStorage.getAllFilm().stream()
                .sorted(this::compare)
                .limit(count).collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        int result = 0;
        if (p0.getLikes().size() < p1.getLikes().size()) {
            result = 1;
        } else {
            result = -1;
        }
        return result;
    }

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }
}
