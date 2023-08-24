package api;

import api.utils.Specification;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static api.utils.Constants.MAIN_URL;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresNoPojoClass  {

    /**
     * Get users list from 2nd page.
     * Make sure that users avatar name starts with user id,
     * and users email ends with "@reqres.in"
     */

    @Test
    public void checkAvatarsAndEmailNoPojoTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());
        Response response = given()
                .get("/api/users?page=2")
                .then().log().all()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        List<Integer> id = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");
        List<String> emails = jsonPath.get("data.email");

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(id.get(i).toString()));
        }
        Assert.assertTrue(emails.stream().allMatch(e -> e.endsWith("@reqres.in")));
    }

    /**
     * Check registration:
     *  1. successful registration with status code
     *  2. unsuccessful registration with status code
     */

    @Test
    public void successRegisterNoPojoTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());

        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");

        Response response = given()
                .body(user)
                .when()
                .post("api/register/")
                .then().log().all()

//                use without Response interface
//                .body("id", equalTo(4))
//                .body("token", equalTo("QpwL5tke4Pnpja7X4"))

                .extract().response();

        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assert.assertEquals(4, id);
        Assert.assertEquals("QpwL5tke4Pnpja7X4", token);
    }

    @Test
    public void unSuccessRegisterNoPojoTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecError400());

        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");

        given()
                .body(user)
                .when()
                .post("api/register/")
                .then().log().all()
                .body("error", equalTo("Missing password"));
    }
}
