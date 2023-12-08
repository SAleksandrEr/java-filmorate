package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Directors;

import java.util.List;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;
    private final FilmStorage filmStorage;

    public DirectorService(DirectorStorage directorStorage, FilmStorage filmStorage) {
        this.directorStorage = directorStorage;
        this.filmStorage = filmStorage;
    }

    public List<Directors> getAllDirectors() {
        List<Directors> directors = directorStorage.getAllDirectors();
        log.info("Все режиссеры получены {}", directors);
        return directors;
    }

    public Directors getDirectorByID(Long id) {
        Directors director = directorStorage.getDirectorByID(id);
        log.info("Получен режиссёр по ID {}", id);
        return director;
    }

    public Directors updateDirector(Directors director) {
        if (director != null) {
            directorStorage.updateDirector(director);
            log.info("Режиссёр обновлен {}", director);
        } else {
            throw new ValidationException("Некорретный формат данных режиссера!");
        }
        return director;
    }

    public Directors createDirector(Directors director) {
        Directors directors = directorStorage.createDirector(director);
        log.info("Режиссёр создан {}", director);
        return directors;
    }

    public void deleteDirector(Long id) {
        directorStorage.getDirectorByID(id);
        log.info("Режиссёр удален с ID {}", id);
        directorStorage.deleteDirector(id);
    }

    public List<Directors> createDirectorsFilm(List<Directors> director, Long idFilm) {
        List<Directors> directors = directorStorage.createDirectorsFilm(director, idFilm);
        log.info("Режиссёры добавлены для фильма с ID {}", idFilm);
        return directors;
    }

    public List<Directors> updateDirectorsFilm(List<Directors> director, Long idFilm) {
        filmStorage.getFilmsId(idFilm);
        if (director.isEmpty() || !directorStorage.getFilmDirectors(idFilm).isEmpty()) {
           directorStorage.deleteDirectorsFilm(idFilm);
        }
        if (!director.isEmpty()) {
            createDirectorsFilm(director, idFilm);
        }
        log.info("Режиссёры обновлены для фильма с ID {}", idFilm);
        return director;
    }
}
