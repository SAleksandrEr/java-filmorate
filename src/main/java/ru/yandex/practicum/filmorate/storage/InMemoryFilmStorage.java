package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);

    private long generationId = 0;

    private final Map<Long, Film> storageFilm = new HashMap<>();

    public Film createFilm(Film film) {
        validate(film);
        film.setId(generationIdUnit());
        storageFilm.put(film.getId(), film);
        log.info("The film was created {}",film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (storageFilm.get(film.getId()) != null) {
            validate(film);
            storageFilm.put(film.getId(), film);
            log.info("The film was update {}",film);
        } else {
            log.warn("Exception, Data not found  id:{}", film.getId());
            throw new DataNotFoundException("Data not found " + film.getId());
        }
        return film;
    }

    public List<Film> getAllFilm() {
        List<Film> list = new ArrayList<>(storageFilm.values());
        log.info("The film was get all {}", list);
        return list;
    }

    public Film getFilmsId(Long id) {
        Film film = storageFilm.get(id);
        log.info("The film was get of ID {}", film);
        return film;
    }

    public void removeAllFilms() {
    }

    public void removeIdFilms(Long id) {
    }

    private void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }

    private long generationIdUnit() {
        return ++generationId;
    }
}
