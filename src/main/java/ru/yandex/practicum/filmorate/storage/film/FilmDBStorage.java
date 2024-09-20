package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbc;

    @Override
    public Film createFilm(Film film) {
        String queryInsert = "";
        int id = 0;
        Integer mpaId = null;
        if (film.getMpa() != null) {
            mpaId = film.getMpa().getId();
        }
        if (checkNewFilm(film)) {
            queryInsert = "insert into films (name, description, release_date, duration, rating_id) values(?, ?, ?, ?, ?)";
            id = insert(queryInsert,film.getName(), film.getDescription(), film.getReleaseDate(),film.getDuration(), mpaId);
        } else {
            throw new ValidationException("Данные заданы неверно");
        }
        film.setId(id);
        if (film.getGenres() != null) {
            for (Genre g: film.getGenres()) {
                queryInsert = "insert into genres_of_films (film_id, genre_id) values(?, ?)";
                insert(queryInsert, film.getId(), g.getId());
            }
        }
        return findById(id).orElseThrow();
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbc.query(
                "select f.*, r.mpa from films f " +
                     "left join rating r on r.id = f.rating_id ",mapFilms());

        HashMap<Integer,List<Integer>> likes = new HashMap<>();
        jdbc.query("select f.id, l.user_id " +
                "from films f " +
                "left join likes l on l.film_id = f.id " +
                "order by f.id", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!likes.containsKey(rs.getInt("id")))
                    likes.put(rs.getInt("id"), new ArrayList<>());
                likes.get(rs.getInt("id")).add(rs.getInt("user_id"));
                return rs.getInt("user_id");
            }
        });
        HashMap<Integer,List<Genre>> genres = new HashMap<>();
        jdbc.query("select f.id as film_id, g.id, g.genre from films f " +
                "left join genres_of_films gof on gof.film_id = f.id " +
                "left join genres g on g.id=gof.genre_id " +
                "order by f.id", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!genres.containsKey(rs.getInt("film_id")))
                    genres.put(rs.getInt("film_id"), new ArrayList<>());
                genres.get(rs.getInt("film_id")).add(new Genre(rs.getInt("id"), rs.getString("genre")));
                return rs.getInt("film_id");
            }
        });
        for (Film f:films) {
            f.getLikes().addAll(likes.get(f.getId()));
            f.getGenres().addAll(genres.get(f.getId()));
        }
        return films;
    }

    @Override
    public Film updateFilm(Film film) {
        Integer mpaId = null;
        if (film.getMpa() != null) {
            mpaId = film.getMpa().getId();
        }
        if (checkNewFilm(film) == true) {
            String queryUpdate = "update films set name=?, description=?, release_date=?, duration=?, rating_id=? where id=?" +
                    "";
            update(queryUpdate, film.getName(),film.getDescription(),film.getReleaseDate(),film.getDuration(),mpaId,film.getId());
            return film;
        } else
            throw new ValidationException("Данные заданы неверно");
    }

    @Override
    public Optional<Film> findById(int id) {
        try {
            Film result = jdbc.queryForObject(
                                    "select f.*, r.mpa, l.user_id from films f " +
                                        "left join likes l on l.film_id = f.id " +
                                        "left join rating r on r.id = f.rating_id " +
                                        "where f.id = ? " +
                                        "order by f.id, l.user_id",mapFilm(),id);
            result.getGenres().addAll(jdbc.query(
                    "select g.id, g.genre from genres_of_films gof " +
                            "left join genres g on g.id=gof.genre_id " +
                            "where gof.film_id = ?",mapGenre(),id));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
    }

    @Override
    public Film likeFilm(int id, int userId) {
        String queryInsert = "insert into likes (user_id, film_id) values(?, ?)";
        insert(queryInsert, userId, id);
        return findById(id).orElseThrow();
    }

    @Override
    public Film deleteLike(int id, int userId) {
        String queryDelete = "delete from likes where film_id=? and user_id=?";
        update(queryDelete, id, userId);
        return findById(id).orElseThrow();
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String queryPopular = "(select film_id " +
                               "from (select film_id,count(user_id) as likes_count " +
                                     "from likes " +
                                     "group by film_id " +
                                     "order by likes_count desc " +
                                     "limit " + count + "))";
        String queryFilms = "select f.*, r.mpa from films f " +
                            "left join rating r on r.id = f.rating_id " +
                            "where f.id in " + queryPopular + "order by f.id";
        String queryLikes = "select l.film_id as id,l.user_id from likes l " +
                "where l.film_id in " + queryPopular + "order by l.film_id";
        String queryGenres = "select gof.film_id, g.id, g.genre " +
                "from genres_of_films gof " +
                "left join genres g on g.id=gof.genre_id " +
                "where gof.film_id in " + queryPopular + "order by gof.film_id";
        List<Film> films = new ArrayList<>(jdbc.query(queryFilms,mapFilms()));
        HashMap<Integer,List<Integer>> likes = new HashMap<>();
        jdbc.query(queryLikes, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!likes.containsKey(rs.getInt("id")))
                    likes.put(rs.getInt("id"), new ArrayList<>());
                likes.get(rs.getInt("id")).add(rs.getInt("user_id"));
                return rs.getInt("user_id");
            }
        });
        HashMap<Integer,List<Genre>> genres = new HashMap<>();
        jdbc.query(queryGenres, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!genres.containsKey(rs.getInt("film_id")))
                    genres.put(rs.getInt("film_id"), new ArrayList<>());
                genres.get(rs.getInt("film_id")).add(new Genre(rs.getInt("id"), rs.getString("genre")));
                return rs.getInt("film_id");
            }
        });
        for (Film f:films) {
            f.getLikes().addAll(likes.get(f.getId()));
            f.getGenres().addAll(genres.get(f.getId()));
        }
        List<Film> sortedFilms = new ArrayList<>(films);
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

    private RowMapper<Film> mapFilm() {
        return (rs, rowNum) -> {
            Film film = new Film();
            Set<Integer> friends = new HashSet<>();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new MPA(rs.getInt("rating_id"),rs.getString("mpa")));
            do {
                film.getLikes().add(rs.getInt("user_id"));
            } while (rs.next());
            return film;
        };
    }

    private RowMapper<Film> mapFilms() {
        return (rs, rowNum) -> {
            Film film = new Film();
            Set<Integer> friends = new HashSet<>();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new MPA(rs.getInt("rating_id"),rs.getString("mpa")));
            return film;
        };
    }

    private RowMapper<Genre> mapGenre() {
        return (rs, rowNum) -> {
            return new Genre(rs.getInt("id"),rs.getString("genre"));
        };
    }

    private RowMapper<Integer> mapMPA() {
        return (rs, rowNum) -> {
            return rs.getInt("id");
        };
    }

    private int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new ValidationException("Ошибка при добавлении");
        }

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            return id;
        } else {
            throw new ValidationException("Не удалось добавить данные");
        }
    }

    private void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Неправильный запрос");
        }
    }
}
