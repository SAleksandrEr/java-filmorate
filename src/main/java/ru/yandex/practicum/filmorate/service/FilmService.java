package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    private final UserService userService;

   @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film findFilmsId(Long id) {
        if ((filmStorage.getFilmsId(id) == null)) {
            throw new DataNotFoundException("FilmID");
        }
        log.info("The film was get of ID {} ", id);
        return filmStorage.getFilmsId(id);
    }

    public Film createLikeFilm(Long id, Long userId) {
        if ((filmStorage.getFilmsId(id) == null) || (userService.findUsersId(userId) == null)) {
            throw new DataNotFoundException("FilmID");
        }
        Film film = filmStorage.getFilmsId(id);
        film.setLikes(userId);
        log.info("The user liked the movie {} filme - ", film);
        return film;
    }

    public Film deleteLikeId(Long id, Long userId) {
        if ((filmStorage.getFilmsId(id) == null) || (userService.findUsersId(userId) == null)) {
            throw new DataNotFoundException("FilmID");
        }
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
}
