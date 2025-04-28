package ru.netology.test;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.netology.data.ApiHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.mode.User;

import static groovy.json.JsonOutput.prettyPrint;


@Slf4j
public class ApiTest {
    private static String authToken;
    private static User user;

    @BeforeAll
    public static void setup() {
        user = DataHelper.getTestUser();
        ApiHelper.Login(user);
        authToken = ApiHelper.getToken(user);
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
        user = DataHelper.getTestUser();
        var cardlist = SQLHelper.getCardsByUID(user.getId());
        var depoCard = cardlist.get(0);
        var withdrCard = cardlist.get(1);
        var tranferAmount = DataHelper.genAmount(withdrCard);
        var depoInitialBalanceinRub = (depoCard.getBalance_in_kopecks() / 100);
        var withdrCardInitialBalanceRub = (withdrCard.getBalance_in_kopecks() / 100);

        ApiHelper.transferMoney(withdrCard, depoCard, String.valueOf(tranferAmount), authToken);

        Assertions.assertEquals(depoInitialBalanceinRub + tranferAmount,
                SQLHelper.requestCardBalanceInRub(depoCard) );
        Assertions.assertEquals(withdrCardInitialBalanceRub - tranferAmount,
                SQLHelper.requestCardBalanceInRub(withdrCard) );
    }
}
