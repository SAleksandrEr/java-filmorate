package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Directors;

import java.util.List;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Directors> getAllDirectors() {
        List<Directors> directors = directorStorage.getAllDirectors();
        log.info("The all directors get {}", directors);
        return directors;
    }

    public Directors getDirectorByID(Long id) {
        Directors director = directorStorage.getDirectorByID(id);
        log.info("The director was get of ID {}", id);
        return director;
    }

    public Directors updateDirector(Directors director) {
        Directors directors = directorStorage.updateDirector(director);
        log.info("The director was update {}", director);
        return directors;
    }

    public Directors createDirector(Directors director) {
        Directors directors = directorStorage.createDirector(director);
        log.info("The director was create {}", director);
        return directors;
    }

    public void deleteDirector(Long id) {
        directorStorage.getDirectorByID(id);
        log.info("The director was delete of ID {}", id);
        directorStorage.deleteDirector(id);
    }
}
