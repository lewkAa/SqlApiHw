package ru.netology.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.ApiHelper;
import ru.netology.data.SQLHelper;
import ru.netology.mode.User;

import static groovy.json.JsonOutput.prettyPrint;


@Slf4j
public class ApiTest {
    private static String authToken;
    private static User user;

    @BeforeAll
    public static void setup() {
        user = SQLHelper.getUserByLogin("vasya");
        user.setPassword("qwerty123");
        authToken = ApiHelper.getAuthToken(user);
        log.info("Успешная настройка теста. Токен: {}", authToken);
    }

    @AfterAll
    static void cleanDatabase() {
        SQLHelper.cleanDatabase();
    }

    @Test
    @DisplayName("Получение списка карт")
    void shouldGetUserCardList() {
        var cardList = ApiHelper.getCardsList(authToken);
        log.info("Список карт пользователя " + user.getLogin() + ":" + "\n{}", prettyPrint(cardList));
    }

    @Test
    @DisplayName("Перевод с карты на карту")
    void shouldTransferBetweenCards() {
        ApiHelper.transferMoney("5559 0000 0000 0002",
                "5559 0000 0000 0001", 5000, authToken);
        var cardList = ApiHelper.getCardsList(authToken);
        log.info("Список карт пользователя " + user.getLogin() + ":" + "\n{}", prettyPrint(cardList));
    }
}
