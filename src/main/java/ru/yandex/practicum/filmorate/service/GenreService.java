package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genres> getAllGenres() {
        List<Genres> genresList = genreStorage.getAllGenres();
        log.info("Все жанры были получены {} ", genresList);
        return genresList;
    }

    public Genres findGenresId(Long id) {
        Genres genres = genreStorage.findGenresId(id);
        log.info("Жанры были получены по ID {} ", id);
        return genres;
    }

    public List<Genres> createGenresFilm(List<Genres> genres, Long idFilm) {
        List<Genres> genresList = genreStorage.createGenresFilm(genres, idFilm);
        log.info("Жанры созданы для фильма с ID {}", idFilm);
        return genresList;
    }

    public List<Genres> updateGenresFilm(List<Genres> genres, Long idFilm) {
        List<Genres> genresList = genreStorage.updateGenresFilm(genres, idFilm);
        log.info("Жанры обновлены {}", genres.size());
        return genresList;
    }
}
