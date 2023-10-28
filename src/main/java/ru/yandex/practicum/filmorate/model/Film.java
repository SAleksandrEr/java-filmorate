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
import java.util.HashSet;
import java.util.Set;


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
    private int duration;

    private Set<Long> likes = new HashSet<>();

    public void setLikes(Long like) {
        likes.add(like);
    }

    public Set<Long> getLikes() {
        return new HashSet<>(likes);
    }

    public void removeLikeId(Long like) {
        likes.remove(like);
    }
}
