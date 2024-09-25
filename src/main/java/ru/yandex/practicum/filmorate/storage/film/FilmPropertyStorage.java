package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmPropertyStorage {
    private final JdbcTemplate jdbc;

    public List<MPA> getAllMPA() {
        String query = "select * from rating";
        return jdbc.query(query, new RowMapper<MPA>() {
            @Override
            public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
                MPA mpa = new MPA(rs.getInt("id"), rs.getString("mpa"));
                return mpa;
            }
        });
    }

    public MPA getMPA(int id) {
        String query = "select * from rating where id = ?";
        try {
            return jdbc.queryForObject(query, new RowMapper<MPA>() {
                @Override
                public MPA mapRow(ResultSet rs, int rowNum) throws SQLException {
                    MPA mpa = new MPA(rs.getInt("id"), rs.getString("mpa"));
                    return mpa;
                }
            },id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
    }

    public List<Genre> getAllGenres() {
        String query = "select * from genres";
        return jdbc.query(query, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                Genre genre = new Genre(rs.getInt("id"), rs.getString("genre"));
                return genre;
            }
        });
    }

    public Genre getGenre(int id) {
        String query = "select * from genres where id = ?";
        try {
            return jdbc.queryForObject(query, new RowMapper<Genre>() {
                @Override
                public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Genre genre = new Genre(rs.getInt("id"), rs.getString("genre"));
                    return genre;
                }
            },id);
        } catch (EmptyResultDataAccessException ignored) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
    }
}