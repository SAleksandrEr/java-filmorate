package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor(force = true)
public class Film extends Unit {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NonNull
    private LocalDate releaseDate;

    @Min(1)
    private Integer duration;

    private Mpa mpa;

    private List<Genres> genres = new ArrayList<>();

    private List<Directors> directors = new ArrayList<>();
}
