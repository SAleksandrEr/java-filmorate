package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.model.Genres;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);

    private final FilmStorage filmStorage;

    private final GenreService genreService;

    private final LikesStorage likesStorage;

    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmStorage filmStorage, GenreService genreService,
                       LikesStorage likesStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.likesStorage = likesStorage;
        this.userService = userService;
    }

    public Film createFilms(Film film) {
        validate(film);
        Film filmCurrant = filmStorage.createFilm(film);
        filmCurrant.setGenres(genreService.createGenresFilm(film.getGenres(), filmCurrant.getId()));
        log.info("The film was created with ID {}", filmCurrant.getId());
        return filmCurrant;
    }

    public Film updateFilms(Film film) {
        validate(film);
        filmStorage.updateFilm(film);
        genreService.updateGenresFilm(film.getGenres(), film.getId());
        film = findFilmsId(film.getId());
        log.info("The film was update {}",film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> film = filmStorage.getAllFilm();
        log.info("The all films was get {}", film.size());
        return film.stream().peek(filmCurrent -> filmCurrent.setGenres(genreService.getFilmGenres(filmCurrent.getId())))
            .collect(Collectors.toList());
    }

    public Film findFilmsId(Long id) {
        Film film = filmStorage.getFilmsId(id);
        List<Genres> genres = genreService.getFilmGenres(id);
        film.setGenres(genres);
        log.info("The film was get of ID {} ", film);
        return film;
    }

    public Film createLikeFilm(Long id, Long userId) {
        findFilmsId(id);
        userService.findUsersId(userId);
        Film film = findFilmsId(likesStorage.createLikeFilm(id, userId));
        log.info("The user liked the movie {} filme - ", film);
        return film;
    }

    public boolean deleteLikeId(Long id, Long userId) {
            log.info("The user deleted the like FilmID - {}", id);
            return likesStorage.deleteLikeId(id, userId);
    }

    public List<Film> findFilmsOfLikes(Integer count) {
        List<Film> result = likesStorage.findFilmsOfLikes(count);
        log.info("Returns a list of the first count movies by number of likes {}", count);
        return result;
    }

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }
}
