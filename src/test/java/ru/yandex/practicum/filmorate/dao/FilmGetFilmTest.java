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
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)

class FilmGetFilmTest {

    private final JdbcTemplate jdbcTemplate;

    @Test
    void getFilmsId() {
        FilmStorage filmStorage = new FilmDaoImpl(jdbcTemplate);
        Film film = Film.builder()
                .name("Test1")
                .description("description")
                .releaseDate(LocalDate.of(2003,9,21))
                .duration(120)
                .mpa(Mpa.builder().name("G").id(1L).build())
                .genres(new ArrayList<>()).id(1L)
                .build();
        filmStorage.createFilm(film);
        assertThat(filmStorage.getFilmsId(film.getId())).isNotNull().usingRecursiveComparison().isEqualTo(film);
    }
}