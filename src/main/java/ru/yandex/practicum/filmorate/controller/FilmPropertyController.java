package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmPropertyService;
import java.util.List;

@RestController
@AllArgsConstructor
public class FilmPropertyController {
    private final FilmPropertyService filmPropertyService;

    @GetMapping("/mpa")
    public List<MPA> getAllMPA() {
        return filmPropertyService.getAllMPA();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMPA(@PathVariable int id) {
        return filmPropertyService.getMPA(id);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmPropertyService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable int id) {
        return filmPropertyService.getGenre(id);
    }
}
