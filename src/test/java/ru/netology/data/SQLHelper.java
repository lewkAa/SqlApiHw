package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.mode.User;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLHelper {
    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static void updateUsers(String login, String password) {
        var dataSQL = "INSERT INTO users(login, password) VALUES (?, ?);";
        try (var conn = getConnection()) {
            runner.update(conn, dataSQL, login, password);
        }
    }

    @SneakyThrows
    public static long countUsers() {
        var countSQL = "SELECT COUNT(*) FROM users;";
        try (var conn = getConnection()) {
            return runner.query(conn, countSQL, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static User getUserByLogin(String login) {
        var usersSQL = "SELECT * FROM users WHERE login= ?;";
        try (var conn = getConnection()) {
            return runner.query(conn, usersSQL, new BeanHandler<>(User.class), login);
        }
    }

    @SneakyThrows
    public static List<User> getUsers() {
        var usersSQL = "SELECT * FROM users;";
        try (var conn = getConnection()) {
            return runner.query(conn, usersSQL, new BeanListHandler<>(User.class));
        }
    }

    @SneakyThrows
    public static String getCodeByUid(String id) {
        var sql = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC LIMIT 1";
        try (var conn = getConnection()) {
            return runner.query(conn, sql, new ScalarHandler<>(), id);
        }
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (var conn = getConnection()) {
            runner.update(conn, "SET FOREIGN_KEY_CHECKS = 0");
            runner.update(conn, "TRUNCATE TABLE card_transactions");
            runner.update(conn, "TRUNCATE TABLE auth_codes");
            runner.update(conn, "TRUNCATE TABLE cards");
            runner.update(conn, "TRUNCATE TABLE users");
            runner.update(conn, "SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

}