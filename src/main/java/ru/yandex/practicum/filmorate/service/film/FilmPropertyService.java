package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmPropertyStorage;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class FilmPropertyService {
    FilmPropertyStorage filmPropertyStorage;

    public List<MPA> getAllMPA() {
        return filmPropertyStorage.getAllMPA();
    }

    public MPA getMPA(int id) {
        return filmPropertyStorage.getMPA(id);
    }

    public List<Genre> getAllGenres() {
        return filmPropertyStorage.getAllGenres();
    }

    public Genre getGenre(int id) {
        return filmPropertyStorage.getGenre(id);
    }
}
