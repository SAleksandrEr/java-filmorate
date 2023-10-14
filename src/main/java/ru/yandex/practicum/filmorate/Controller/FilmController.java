package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);
    private static long generationId = 0;
    private final Map<Long, Film> storage = new HashMap<>();

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validate(film);
        film.setId(generationIdUnit());
        storage.put(film.getId(), film);
        log.info("The film was created {}",film);
            return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (storage.get(film.getId()) != null) {
            storage.put(film.getId(), film);
            log.info("The film was update {}",film);
        } else {
            log.warn("Exception, Data not found  id:{}", film.getId());
            throw new DataNotFoundException("Data not found " + film.getId());
        }
            return film;
    }

    @GetMapping
    public List<Film> getAllFilm() {
        List<Film> list = new ArrayList<>(storage.values());
        log.info("The film was get all {}", list);
            return list;
    }


    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }
    private long generationIdUnit() {
        return ++generationId;
    }
}
