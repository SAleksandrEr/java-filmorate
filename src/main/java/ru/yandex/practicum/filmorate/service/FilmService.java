package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventsStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.LikesStorage;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
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

    private final EventsStorage eventsStorage;


    @Autowired
    public FilmService(@Qualifier("filmDaoImpl") FilmStorage filmStorage, GenreService genreService,
                       LikesStorage likesStorage, UserService userService, DirectorService directorService, EventsStorage eventsStorage) {
        this.filmStorage = filmStorage;
        this.genreService = genreService;
        this.likesStorage = likesStorage;
        this.userService = userService;
        this.directorService = directorService;
        this.eventsStorage = eventsStorage;
    }

    public Film createFilms(Film film) {
        validate(film);
        Film filmCurrant = filmStorage.createFilm(film);
        genreService.createGenresFilm(film.getGenres(), filmCurrant.getId());
        directorService.createDirectorsFilm(film.getDirectors(), filmCurrant.getId());
        log.info("Фильм создан с ID {}", filmCurrant.getId());
        return filmStorage.getFilmsId(filmCurrant.getId());
    }

    public Film updateFilms(Film film) {
        validate(film);
        genreService.updateGenresFilm(film.getGenres(), film.getId());
        directorService.updateDirectorsFilm(film.getDirectors(), film.getId());
        film = filmStorage.updateFilm(film);
        log.info("Фильм был обновлен с ID {}",film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> film = filmStorage.getAllFilm();
        log.info("Все фильмы были получены {}", film.size());
        return film;
    }

    public Film findFilmsId(Long id) {
        Film film = filmStorage.getFilmsId(id);
        log.info("Получен фильм c ID {} ", film);
        return film;
    }

    public Film createLikeFilm(Long id, Long userId) {
        Film film = findFilmsId(id);
        userService.findUsersId(userId);
        likesStorage.createLikeFilm(id, userId);
        eventsStorage.createUserIdEvents(film.getId(), userId, EventType.LIKE, EventOperation.ADD);
        log.info("Пользователю понравился фильм {} film - ", film);
        return film;
    }

    public boolean deleteLikeId(Long id, Long userId) { // надо подумать!!!!
        boolean status = likesStorage.deleteLikeId(id, userId);
        eventsStorage.createUserIdEvents(id, userId, EventType.LIKE, EventOperation.REMOVE);
        log.info("Пользователь удалил лайк FilmID - {}", id);
        return status;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        Collection<Film> listOfUserFilms = filmStorage.getCommonFilms(userId);
        Collection<Film> listOfFriendFilms = filmStorage.getCommonFilms(friendId);
        Set<Film> commonList = new HashSet<>(listOfFriendFilms);
        commonList.retainAll(listOfUserFilms);
        return new ArrayList<>(commonList);
    }

    public void filmDeleteById(Long filmId) { //метод удаления фильма по id
        if (filmStorage.getFilmsId(filmId) == null) {
            throw new DataNotFoundException("Фильм с таким id не существует " + filmId);
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
        log.info("Получен список фильмов по запросу {} ", query + " из " + by);
        return filmStorage.searchNameFilmsAndDirectors(query, by);
    }

    public List<Film> getPopularFilms(Long count, Long genreId, Long year) {
        log.info("Получены популярные фильмы");
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("Дата фильма не верна {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }
}
