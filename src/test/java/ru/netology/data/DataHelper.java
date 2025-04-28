package ru.netology.data;

import com.github.javafaker.Faker;
import ru.netology.mode.Card;
import ru.netology.mode.User;

public class DataHelper {
    static Faker faker = new Faker();

    public static int genAmount(Card card) {
        int limit = card.getBalance_in_kopecks() / 100;
        return faker.number().numberBetween(1, limit + 1);
    }

    public static User getTestUser() {
        User validUser = SQLHelper.getUserByLogin("vasya");
        validUser.setPassword("qwerty123");
        return validUser;
    }
}
