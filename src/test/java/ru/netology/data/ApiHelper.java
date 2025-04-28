package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.netology.mode.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class ApiHelper {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL).build();

    public static void Login(User user) {
        Map<String, String> loginData = Map.of(
                "login", user.getLogin(),
                "password", user.getPassword()
        );

        given()
                .spec(requestSpec)
                .body(loginData)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public static String getToken(User user) {
        Map<String, String> authData = Map.of(
                "login", user.getLogin(),
                "code", SQLHelper.getCodeByUid(user.getId())
        );

        return given()
                .spec(requestSpec)
                .body(authData)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");
    }

    public static String getCardsList(String authToken) {
        Response response = given()  // Сохраняем ответ в переменную
                .spec(requestSpec)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.getBody().asString();
    }

    public static void transferMoney(Card withdrawCard, Card depositCard,
                                     String amount, String authToken) {

        Map<String, String> transferData = Map.of(
                "from", withdrawCard.getNumber(),
                "to", depositCard.getNumber(),
                "amount", amount
        );

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + authToken)
                .body(transferData)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }
}
