package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Qualifier
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sqlQuery = "select id, email, login, username, birthday from users";
        List<User> allUsers = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser);
        System.out.println(allUsers);
        return allUsers;
    }


    public User create(User user) {
        String sqlQuery = "insert into users (email, login, username, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            stmt.setString(3, user.getName());
            final LocalDate birthday = user.getBirthday();
            if (birthday == null) {
                stmt.setNull(4, Types.DATE);
            } else {
                stmt.setDate(4, Date.valueOf(birthday));
            }
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    public User update(User user) {
        String sqlQuery = "update users set " +
                "EMAIL = ?, LOGIN = ?, USERNAME = ?, BIRTHDAY = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        return user;
    }


    public User get(int userId) {
        String sqlQuery = "select id, email, login, username, birthday " +
                "from users " +
                "where id = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, userId);
        if (users.size() != 1) {
            return null;
        }
        return users.get(0);
    }


    private static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("username"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    public void addFriend(User user, User friend) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship");
        Map<String, Object> values = new HashMap<>();
        values.put("user_id", user.getId());
        values.put("friend_id", friend.getId());
        simpleJdbcInsert.execute(values);
    }


    public void deleteFriend(User user, User friend) {
        String sqlQuery = "delete from friendship " +
                "where friend_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery
                , friend.getId()
                , user.getId());
    }

    public List<User> getAllFriends(User user) {
        String sqlQuery = "select id, email, login, username, birthday " +
                "from FRIENDSHIP AS f " +
                "join USERS U on U.ID = f.FRIEND_ID " +
                "where f.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, user.getId());
    }

    public List<User> getCommonFriends(User user, User other) {
        String sqlQuery = "select * " +
                "from users AS u, " +
                "friendship AS f1, " +
                "friendship AS f2 " +
                "where f1.user_id = ? " +
                "and f2.user_id = ? " +
                "and f1.friend_id = f2.friend_id " +
                "and f1.friend_id = u.id";
        return jdbcTemplate.query(sqlQuery, UserDbStorage::mapRowToUser, user.getId(), other.getId());
    }
}