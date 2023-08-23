package api;

import io.restassured.http.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

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

        userData.forEach(u -> Assert.assertTrue(u.getAvatar().contains(u.getId().toString())));

        Assert.assertTrue(userData.stream().allMatch(u -> u.getEmail().endsWith("reqres.in")));

        List<String> avatars = userData.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = userData.stream().map(u -> u.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(id.get(i)));
        }
    }
}
