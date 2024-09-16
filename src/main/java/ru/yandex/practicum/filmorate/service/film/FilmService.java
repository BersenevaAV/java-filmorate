package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        log.info("Пришел запрос на создание фильма с name = {}",film.getName());
        return filmStorage.createFilm(film);
    }

    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.getAll());
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Optional<Film> findById(int id) {
        return filmStorage.findById(id);
    }


    public Film likeFilm(int id, int userId) {
        if (userStorage.checkIdUser(userId) == true)
            return filmStorage.likeFilm(id, userId);
        else
            return null;
    }

    public Film deleteLike(int id, int userId) {
        if (userStorage.checkIdUser(userId) == true)
            return filmStorage.deleteLike(id, userId);
        else
            return null;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }
}
