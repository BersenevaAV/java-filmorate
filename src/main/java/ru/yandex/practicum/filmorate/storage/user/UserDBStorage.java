package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbc;

    @Override
    public User createUser(User user) {
        String queryInsert = "insert into users (email, login, name, birthday) values(?, ?, ?, ?)";
        int id = 0;
        if (checkNewUser(user) == true) {
            id = insert(queryInsert, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
            if (id == 0) {
                throw new ValidationException("Не удалось добавить данные");
            } else {
                user.setId(id);
                return user;
            }
        } else
            throw new ValidationException("Данные заданы неверно");
    }

    @Override
    public List<User> getAll() {
        String query = "select * from users";
        List<User> users =  jdbc.query(query, mapUsers());
        HashMap<Integer,List<Integer>> friends = new HashMap<>();
        jdbc.query("select u.id , fr.friend_id from users u " +
                       "left join friends fr on fr.user_id = u.id", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!friends.containsKey(rs.getInt("id")))
                    friends.put(rs.getInt("id"), new ArrayList<>());
                friends.get(rs.getInt("id")).add(rs.getInt("friend_id"));
                return rs.getInt("friend_id");
            }
        });
        for (User u: users) {
            u.getFriends().addAll(friends.get(u.getId()));
        }
        return users;
    }

    @Override
    public User updateUser(User user) {
        if (!checkIdUser(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Такого пользователя нет");
        }
        if (checkNewUser(user)) {
            String queryUpdate = "update users set email=?, login=?, name=?, birthday=? where id=?";
            update(queryUpdate, user.getEmail(),user.getLogin(),user.getName(),user.getBirthday(),user.getId());
            return user;
        } else
            throw new ValidationException("Данные заданы неверно");
    }

    @Override
    public Optional<User> findById(int id) {
        String query = "select u.* from users u where u.id = ?";
        try {
            User result = jdbc.queryForObject(query,mapUsers(),id);
            result.getFriends().addAll(jdbc.query("select friend_id from friends where user_id = ?", new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt("friend_id");
                }
            }, id));
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Объект не найден");
        }
    }

    @Override
    public User addInFriends(int id, int friendId) {
        if (checkIdUser(id) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не существует");
        Set<Integer> friends = findById(friendId).orElseThrow().getFriends();
        if (friends.contains(id))
            update("update friends set friendship = true where user_id = ? and friend_id = ?",friendId,id);
        else {
            String queryInsert = "insert into friends (user_id,friend_id,friendship) values(?, ?, false)";
            insert(queryInsert, id, friendId);
        }
        return findById(id).orElseThrow();
    }

    @Override
    public User deleteFromFriends(int id, int friendId) {
        if (checkIdUser(id) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь (" + id + ") не существует");
        if (checkIdUser(friendId) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь (" + friendId + ") не существует");
        Set<Integer> friendsOfFriend = findById(friendId).orElseThrow().getFriends();
        Set<Integer> friendsOfUser = findById(id).orElseThrow().getFriends();
        if (!friendsOfUser.isEmpty() && friendsOfUser.contains(friendId)) {
            String queryDelete = "delete from friends where user_id=? and friend_id=?";
            update(queryDelete,id,friendId);
        }
        if (!friendsOfFriend.isEmpty() && friendsOfFriend.contains(friendId))
            update("update friends set friendship = false where user_id = ? and friend_id = ?",friendId,id);
        return findById(id).orElseThrow();
    }

    @Override
    public List<User> getFriends(int id) {
        if (checkIdUser(id) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не существует");
        String queryFriends = "select u.* from users u left join friends f on f.friend_id = u.id where f.user_id = ?";
        List<User> friends =  jdbc.query(queryFriends, mapUsers(), id);
        HashMap<Integer,List<Integer>> friendsOfFriends = new HashMap<>();
        jdbc.query("select u.id , fr.friend_id from users u " +
                "left join friends fr on fr.user_id = u.id", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (!friendsOfFriends.containsKey(rs.getInt("id")))
                    friendsOfFriends.put(rs.getInt("id"), new ArrayList<>());
                friendsOfFriends.get(rs.getInt("id")).add(rs.getInt("friend_id"));
                return rs.getInt("friend_id");
            }
        });
        for (User u: friends) {
            u.getFriends().addAll(friendsOfFriends.get(u.getId()));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        if (checkIdUser(id) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь (" + id + ") не существует");
        if (checkIdUser(otherId) == false)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь (" + otherId + ") не существует");
        Set<Integer> friends1 = findById(id).orElseThrow().getFriends();
        Set<Integer> friends2 = findById(otherId).orElseThrow().getFriends();
        Set<Integer> result = new HashSet<>(friends1);
        result.retainAll(friends2);
        return getAll().stream()
                .filter(x -> result.contains(x.getId()))
                .toList();
    }

    @Override
    public boolean checkIdUser(int id) {
        int idUser = -1;
        try {
            idUser = jdbc.queryForObject(
                    "select id from users where id = ?", new RowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt("id");
                        }
                    },id);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
        if (idUser < 0)
            return false;
        else
            return true;
    }

    private RowMapper<User> mapUser() {
        return (rs, rowNum) -> {
            User user = new User();
            Set<Integer> friends = new HashSet<>();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            do {
                int friendId = rs.getInt("friend_id");
                if (friendId != 0)
                    friends.add(friendId);
            } while (rs.next());
            user.setFriends(friends);
            return user;
        };
    }

    private RowMapper<User> mapUsers() {
        return (rs, rowNum) -> {
            User user = new User();
            Set<Integer> friends = new HashSet<>();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }

    private boolean checkNewUser(User newUser) {
        boolean trueEmail = !newUser.getEmail().isEmpty() && newUser.getEmail().contains("@");
        boolean trueLogin = !newUser.getLogin().isEmpty() && !newUser.getLogin().contains(" ");
        boolean trueBirthday = newUser.getBirthday().isBefore(LocalDate.now());
        if (trueEmail && trueLogin && trueBirthday) {
            if (newUser.getName() == null)
                newUser.setName(newUser.getLogin());
            return true;
        } else
            return false;
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
