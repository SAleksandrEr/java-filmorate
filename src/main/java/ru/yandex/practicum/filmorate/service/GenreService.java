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
        log.info("The all genres was get {} ", genresList);
        return genresList;
    }

    public Genres findGenresId(Long id) {
        Genres genres = genreStorage.findGenresId(id);
        log.info("The genres was get of ID {} ", id);
        return genres;
    }

    public List<Genres> getFilmGenres(Long idFilm) {
        List<Genres> genresList = genreStorage.getFilmGenres(idFilm);
        log.info("The genres was get of film with ID {} ", idFilm);
        return genresList;
    }

    public List<Genres> createGenresFilm(List<Genres> genres, Long idFilm) {
        List<Genres> genresList = genreStorage.createGenresFilm(genres, idFilm);
        log.info("The genres was created for film with ID {}", idFilm);
        return genresList;
    }

    public List<Genres> updateGenresFilm(List<Genres> genres, Long idFilm) {
        List<Genres> genresList = genreStorage.updateGenresFilm(genres, idFilm);
        log.info("The Genre was update {}", genres.size());
        return genresList;
    }
}
