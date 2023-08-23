package api;

import api.sort_compairing.ColorsData;
import api.registration.Register;
import api.registration.SuccessReg;
import api.registration.UnSuccessReg;
import api.registration.UserData;
import api.utils.Specification;
import api.time_compairing.UserTime;
import api.time_compairing.UserTimeResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static api.utils.Constants.MAIN_URL;
import static io.restassured.RestAssured.given;

public class ReqresPojoTest {

    /**
     * Get users list from 2nd page.
     * Make sure that users avatar name starts with user id,
     * and users email ends with "@reqres.in"
     */

    @Test
    public void checkAvatarAndIdTest() {

        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());

        List<UserData> userData = given()
                .when()
                .get("/api/users?page=2")
                .then()
                .log()
                .all()
                .extract().body().jsonPath().getList("data", UserData.class);

        userData.forEach(u -> Assert.assertTrue(u.getAvatar().contains(u.getId().toString())));

        Assert.assertTrue(userData.stream().allMatch(u -> u.getEmail().endsWith("@reqres.in")));

        List<String> avatars = userData.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = userData.stream().map(u -> u.getId().toString()).collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(id.get(i)));
        }
    }

    /**
     * Check registration:
     *  1. successful registration with status code
     *  2. unsuccessful registration with status code
     */
    @Test
    public void successRegisterTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());

        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";

        Register user = new Register("eve.holt@reqres.in", "pistol");

        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register/")
                .then().log().all()
                .extract()
                .as(SuccessReg.class);

        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessRegTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecError400());

        Register user = new Register("sydney@fife", "");

        UnSuccessReg unSuccessReg = given()
                .body(user)
                .when()
                .post("api/register/")
                .then().log().all()
                .extract()
                .as(UnSuccessReg.class);

        Assert.assertEquals("Missing password", unSuccessReg.getError());
    }

    /**
     * Get list of resources and make sure that it sorted by year (asc)
     */

    @Test
    public void sortedYearTest(){
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());

        List<ColorsData> colorsData = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract()
                .body().jsonPath().getList("data", ColorsData.class);
        List<Integer> years = colorsData.stream().map(c -> c.getYear()).collect(Collectors.toList());
        List<Integer> sortYears = years.stream().sorted().collect(Collectors.toList());

        Assert.assertEquals(sortYears, years);

    }

    /**
     * Try to delete user with id=2 and compare status code
     */

    @Test
    public void deleteUserTest(){
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecUnique(204));

        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    /**
     * Try to update user data and compare dates
     */

    @Test
    public void timeTest() {
        Specification.installSpec(Specification.requestSpecification(MAIN_URL), Specification.responseSpecOk200());

        UserTime userTime = new UserTime("morpheus", "zion resident");
        UserTimeResponse userTimeResponse = given()
                .body(userTime)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "(.{5})$";

        String responseTime = userTimeResponse.getUpdatedAt().replaceAll(regex,"");
        String currentTime = Clock.systemUTC().instant().toString().substring(0, responseTime.length());
        Assert.assertEquals(currentTime, responseTime);

    }
}
