package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();


    private int idGenerator = 1;

    public Film createFilm(Film film) {
        if (checkNewFilm(film) == true) {
            film.setId(idGenerator++);
            films.put(film.getId(),film);
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        return film;
    }

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId()) && checkNewFilm(film) == true) {
            films.put(film.getId(),film);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
        return film;
    }

    public Optional<Film> findById(int id) {
        return films.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst();
    }

    public Film likeFilm(int id, int userId) {
        if (films.containsKey(id)) {
            films.get(id).getLikes().add(userId);
            return films.get(id);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
    }

    public Film deleteLike(int id, int userId) {
        if (films.containsKey(id) == true && films.get(id).deleteLike(userId)) {
            return films.get(id);
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> sortedFilms = new ArrayList<>(films.values());
        sortedFilms.sort((f1,f2) -> (f2.getCountLikes() - f1.getCountLikes()));
        return sortedFilms.stream().limit(count).toList();
    }

    private boolean checkNewFilm(Film newFilm) {
        LocalDate firstDate = LocalDate.of(1895,12,28);
        boolean trueName = !newFilm.getName().isEmpty();
        boolean trueDescription = newFilm.getDescription().length() < 200;
        boolean trueReleaseDate = newFilm.getReleaseDate().isAfter(firstDate);
        boolean trueDuration = newFilm.getDuration() > 0;
        if (trueName && trueDescription && trueReleaseDate && trueDuration)
            return true;
        else
            return false;
    }
}
