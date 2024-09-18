package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getAll();

    Film updateFilm(Film film);

    Optional<Film> findById(int id);

    Film likeFilm(int id, int userId);

    Film deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
