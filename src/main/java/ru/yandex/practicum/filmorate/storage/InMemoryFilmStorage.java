package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private long generationId = 0;

    private final Map<Long, Film> storageFilm = new HashMap<>();

    public Film createFilm(Film film) {
        film.setId(generationIdUnit());
        storageFilm.put(film.getId(), film);
        log.info("The film was created {}",film);
        return film;
    }

    public Film updateFilm(Film film) {
        if (storageFilm.get(film.getId()) != null) {
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
        if (film == null) {
            throw new DataNotFoundException("FilmID");
        }
        log.info("The film was get of ID {}", film);
        return film;
    }

    private long generationIdUnit() {
        return ++generationId;
    }
}
