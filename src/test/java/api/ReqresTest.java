package api;

import io.restassured.http.ContentType;
import org.junit.Test;

import java.util.List;

import static api.Constants.MAIN_URL;
import static io.restassured.RestAssured.given;

public class ReqresTest {

    @Test
    public void checkAvatarAndIdTest() {
        List<UserData> userData = given()
                .when()
                .contentType(ContentType.JSON)
                .get(MAIN_URL + "/api/users?page=2")
                .then()
                .log()
                .all()
                .extract().body().jsonPath().getList("data", UserData.class);
    }
}
