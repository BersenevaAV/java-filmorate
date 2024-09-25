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

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Film createFilm(Film film) {
        log.info("Пришел запрос на создание фильма с name = {}",film.getName());
        return filmStorage.createFilm(film);
    }

    public List<Film> getAll() {
        log.info("Пришел запрос для получения всех фильмов");
        return new ArrayList<>(filmStorage.getAll());
    }

    public Film updateFilm(Film film) {
        log.info("Пришел запрос на обновление фильма с id = {}",film.getId());
        return filmStorage.updateFilm(film);
    }

    public Optional<Film> findById(int id) {
        log.info("Пришел запрос на поиск фильма с id = {}",id);
        return filmStorage.findById(id);
    }


    public Film likeFilm(int id, int userId) {
        log.info("Пришел запрос: пользователь(id={}) поставил лайк фильму с id = {} ", userId, id);
        if (userStorage.checkIdUser(userId) == true)
            return filmStorage.likeFilm(id, userId);
        else
            return null;
    }

    public Film deleteLike(int id, int userId) {
        log.info("Пришел запрос: пользователь(id={}) удалил лайк у фильма с id = {} ", userId, id);
        if (userStorage.checkIdUser(userId) == true)
            return filmStorage.deleteLike(id, userId);
        else
            return null;
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Пришел запрос для получения популярных фильмов в количестве = {}", count);
        return filmStorage.getPopularFilms(count);
    }
}
