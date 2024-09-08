package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private final FilmController filmController = new FilmController();
    private Film film1;

    @BeforeEach
    public void beforeEach() {
        film1 = new Film();
    }

    @Test
    public void createCorrectFilm() {
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2010,2,1));
        film1.setDuration(120);
        assertEquals(film1,filmController.createFilm(film1), "Возвращаемый объект не соответствует добавляемому");
    }

    @Test
    public void createUncorrectFilm() {
        film1.setName("Film2");
        film1.setDescription("Desc2");
        film1.setReleaseDate(LocalDate.of(1890,12,11));
        film1.setDuration(120);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film1), "Должно вызываться ValidationException");
    }

    @Test
    public void getFilms() {
        film1.setName("Film3");
        film1.setDescription("Desc3");
        film1.setReleaseDate(LocalDate.of(2020,6,22));
        film1.setDuration(160);
        int n = filmController.getAll().size();
        filmController.createFilm(film1);
        assertEquals(n + 1, filmController.getAll().size(), "При добавлении количество фильмов не совпадает");
        Film film2 = new Film();
        film2.setId(film1.getId());
        film2.setName("Film4");
        film2.setDescription("Desc4");
        film2.setReleaseDate(LocalDate.of(2001,10,30));
        film2.setDuration(100);
        filmController.updateFilm(film2);
        assertEquals(n + 1, filmController.getAll().size(), "При обновлении количество фильмов не совпадает");
    }
}