package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private int duration;
    private MPA mpa;
    private Set<Integer> likes = new HashSet();
    private final LinkedHashSet<Genre> genres = new LinkedHashSet();

    public boolean deleteLike(int userId) {
        return likes.remove(userId);
    }

    public int getCountLikes() {
        return likes.size();
    }
}
