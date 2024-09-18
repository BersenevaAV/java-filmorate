package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes = new HashSet();

    public boolean deleteLike(int userId) {
        return likes.remove(userId);
    }

    public int getCountLikes() {
        return likes.size();
    }

}
