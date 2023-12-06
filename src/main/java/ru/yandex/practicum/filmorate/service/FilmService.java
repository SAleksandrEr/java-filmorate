package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);

    private final FilmStorage filmStorage;

    private final GenreService genreService;

    private final LikesStorage likesStorage;

    private final UserService userService;

    private final DirectorService directorService;


    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmStorage filmStorage, GenreService genreService,
                       LikesStorage likesStorage, UserService userService, DirectorService directorService) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.likesStorage = likesStorage;
        this.userService = userService;
        this.directorService = directorService;
    }

    public Film createFilms(Film film) {
        validate(film);
        Film filmCurrant = filmStorage.createFilm(film);
        genreService.createGenresFilm(film.getGenres(), filmCurrant.getId());
        directorService.createDirectorsFilm(film.getDirectors(), filmCurrant.getId());
        log.info("The film was created with ID {}", filmCurrant.getId());
        return filmStorage.getFilmsId(filmCurrant.getId());
    }

    public Film updateFilms(Film film) {
        validate(film);
        genreService.updateGenresFilm(film.getGenres(), film.getId());
        directorService.updateDirectorsFilm(film.getDirectors(), film.getId());
        film = filmStorage.updateFilm(film);
        log.info("The film was update {}",film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> film = filmStorage.getAllFilm();
        log.info("The all films was get {}", film.size());
        return film;
    }

    public Film findFilmsId(Long id) {
        Film film = filmStorage.getFilmsId(id);
        log.info("The film was get of ID {} ", film);
        return film;
    }

    public Film createLikeFilm(Long id, Long userId) {
        Film film = findFilmsId(id);
        userService.findUsersId(userId);
        likesStorage.createLikeFilm(id, userId);
        log.info("The user liked the movie {} film - ", film);
        return film;
    }

    public boolean deleteLikeId(Long id, Long userId) {
            log.info("The user deleted the like FilmID - {}", id);
            return likesStorage.deleteLikeId(id, userId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        Collection<Film> listOfUserFilms = filmStorage.getCommonFilms(userId);
        Collection<Film> listOfFriendFilms = filmStorage.getCommonFilms(friendId);
        Set<Film> commonList = new HashSet<>(listOfFriendFilms);
        commonList.retainAll(listOfUserFilms);
        return new ArrayList<>(commonList);
    }

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }

    public void filmDeleteById(Long filmId) { //метод удаления фильма по id
        if (filmStorage.getFilmsId(filmId) == null) {
            throw new DataNotFoundException("Фильм с такой айди не существует");
        }
        filmStorage.filmDeleteById(filmId);
    }

    public List<Film> getFilmsByDirectorId(Long id, String sortBy) {
        if (directorService.getDirectorByID(id) == null) {
            throw new DataNotFoundException("Режиссер с id = " + id + " не найден!");
        }
        return filmStorage.getFilmsByDirectorId(id, sortBy);
    }

    public List<Film> searchNameFilmsAndDirectors(String query, List<String> by) {
        log.info("Returns a list of the movies by query {} ", query + " from " + by);
        return filmStorage.searchNameFilmsAndDirectors(query, by);
    }

    public List<Film> getPopularFilms(Long count, Long genreId, Long year) {
        log.info("Return popular films");
        return filmStorage.getPopularFilms(count, genreId, year);
    }
}
