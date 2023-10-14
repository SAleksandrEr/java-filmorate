package ru.yandex.practicum.filmorate.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private static final LocalDate DATA_RELIES_MIN = LocalDate.of(1895,12,28);

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        super.making(film);
        log.info("The film was created {}",film);
            return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        super.update(film);
        log.info("The film was update {}",film);
            return film;
    }

    @GetMapping
    public List<Film> getAllFilm() {
        List<Film> list = super.getAll();
        log.info("The film was get all {}", list);
            return list;
    }

    @Override
    public void validate(Film data) {
        if (data.getReleaseDate().isBefore(DATA_RELIES_MIN)) {
            log.warn("The film date is not correct {} ",data.getReleaseDate());
            throw new ValidationException("Invalid date" + data);
        }
    }
}
