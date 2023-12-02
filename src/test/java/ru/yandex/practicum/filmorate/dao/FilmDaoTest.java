package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
class FilmDaoTest {

    private final FilmDaoImpl filmStorage;

    @Test
    public void getFilmsAndCreateTest() {
        Film film = Film.builder()
                .name("Test1")
                .description("description")
                .releaseDate(LocalDate.of(2003, 9, 21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>())
                .build();
        Film newfilm = filmStorage.createFilm(film);
        assertThat(filmStorage.getFilmsId(newfilm.getId())).isNotNull().usingRecursiveComparison()
                .comparingOnlyFields(String.valueOf(film.getId())).isEqualTo(film);
    }

    @Test
    public void updateFilmTest() {
        Film film = Film.builder()
                 .name("Test1")
                 .description("description")
                 .releaseDate(LocalDate.of(2003, 9, 21))
                 .duration(120)
                 .mpa(Mpa.builder().name("G").id(1L).build())
                 .genres(new ArrayList<>())
                .directors(new ArrayList<>())
                 .build();
        film = filmStorage.createFilm(film);
        Film newfilm = Film.builder()
                      .name("Test2")
                      .description("description2")
                      .releaseDate(LocalDate.of(2015, 9, 21))
                      .duration(120)
                      .mpa(Mpa.builder().name("G").id(1L).build())
                      .genres(new ArrayList<>())
                .id(film.getId())
                .directors(new ArrayList<>())
                .build();
        Film updateFilm = filmStorage.updateFilm(newfilm);
        assertThat(updateFilm).isNotNull().usingRecursiveComparison().comparingOnlyFields(String.valueOf(film.getId()))
                  .isEqualTo(film);
    }

        @Test
    public void getAllFilmTset() {
        Film film1 = Film.builder()
                .name("Test1")
                .description("description")
                .releaseDate(LocalDate.of(2001,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>())
                .build();
        filmStorage.createFilm(film1);
        Film film2 = Film.builder()
                .name("Test2")
                .description("description")
                .releaseDate(LocalDate.of(2001,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>())
                .build();
        filmStorage.createFilm(film2);
        List<Film> listFilm = new ArrayList<>();
        listFilm.add(film1);
        listFilm.add(film2);
        assertThat(filmStorage.getAllFilm()).isNotNull().usingRecursiveComparison()
                .comparingOnlyFields(String.valueOf(film2.getId()))
                .isEqualTo(listFilm);
    }
}