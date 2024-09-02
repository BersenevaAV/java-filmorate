package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        if (checkNewFilm(film) == true) {
            film.setId(idGenerator++);
            films.put(film.getId(),film);
            log.info("Пришел запрос на создание фильма с name = {}",film.getName());
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId()) && checkNewFilm(film) == true) {
            films.put(film.getId(),film);
        } else {
             throw new ValidationException("Данные заданы неверно");
        }
        return film;
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
