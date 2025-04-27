package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import ru.netology.mode.User;
import lombok.extern.slf4j.Slf4j;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class ApiHelper {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL).build();

    public static String getAuthToken(User user) {
        given()
                .spec(requestSpec)
                .body("{ \"login\": \"" + user.getLogin() + "\", \"password\": \"" + user.getPassword() + "\" }")
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return given()
                .spec(requestSpec)
                .body("{ \"login\": \"" + user.getLogin() + "\", \"code\": \""
                        + SQLHelper.getCodeByUid(user.getId()) + "\" }")
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

    public static void transferMoney(String fromCardNumber,
                                                 String toCardNumber, int amount, String authToken) {

        String requestBody = String.format(
                "{ \"from\": \"%s\", \"to\": \"%s\", \"amount\": %d }",
                fromCardNumber, toCardNumber, amount
        );

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + authToken)
                .body(requestBody)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }
}
