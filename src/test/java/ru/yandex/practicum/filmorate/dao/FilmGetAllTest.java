package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGetAllTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void getAllFilm() {
        FilmStorage filmStorage = new FilmDaoImpl(jdbcTemplate);
        Film film1 = Film.builder()
                .name("Test1")
                .description("description")
                .releaseDate(LocalDate.of(2001,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>()).id(1L)
                .build();
        film1 = filmStorage.createFilm(film1);
        Film film2 = Film.builder()
                .name("Test2")
                .description("description")
                .releaseDate(LocalDate.of(2001,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>()).id(2L)
                .build();
        film2 = filmStorage.createFilm(film2);
        Film film3 = Film.builder()
                .name("Test3")
                .description("description")
                .releaseDate(LocalDate.of(2001,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>()).id(3L)
                .build();
        film3 = filmStorage.createFilm(film3);
        List<Film> listFilm = new ArrayList<>();
        listFilm.add(film1);
        listFilm.add(film2);
        listFilm.add(film3);
        assertThat(filmStorage.getAllFilm()).isNotNull().usingRecursiveComparison().isEqualTo(listFilm);
    }
}