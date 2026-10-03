package com.framework.tests.api;

import com.framework.api.ApiClient;
import com.framework.api.models.Booking;
import com.framework.api.models.BookingDates;
import com.framework.config.ConfigReader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * End-to-end CRUD against Restful Booker (api.booker.url), a free API that really stores data:
 * the booking created here is read, searched, updated, patched and deleted by the tests that follow
 * (chained with dependsOnMethods). PUT, PATCH and DELETE need the token from POST /auth, sent as a cookie.
 * The tests share state through fields, so do not run this class with parallel="methods".
 */
public class BookingApiTests {

    private static final String BASE_URI = ConfigReader.get("api.booker.url");

    private String token;
    private int bookingId;
    private Booking booking;

    @Test(groups = {"api", "smoke"}, description = "POST /auth with valid credentials returns a token")
    public void createAuthToken() {
        Response response = ApiClient.request(BASE_URI)
                .body(Map.of("username", ConfigReader.get("api.booker.username"),
                        "password", ConfigReader.get("api.booker.password")))
                .post("/auth");

        Assert.assertEquals(response.getStatusCode(), 200);
        token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "No token in response: " + response.asString());
    }

    @Test(groups = {"api", "regression"}, description = "POST /auth with a wrong password is rejected")
    public void authWithInvalidCredentialsIsRejected() {
        Response response = ApiClient.request(BASE_URI)
                .body(Map.of("username", "admin", "password", "wrong-password"))
                .post("/auth");

        // Restful Booker answers 200 and puts the error in the body
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("reason"), "Bad credentials");
        Assert.assertNull(response.jsonPath().getString("token"), "No token expected");
    }

    @Test(groups = {"api", "smoke"}, description = "POST /booking creates a booking")
    public void createBooking() {
        Booking newBooking = new Booking("Api", "Tester" + System.currentTimeMillis(), 150, true,
                new BookingDates("2026-11-01", "2026-11-05"), "Breakfast");

        Response response = ApiClient.request(BASE_URI).body(newBooking).post("/booking");

        Assert.assertEquals(response.getStatusCode(), 200);
        bookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Expected a booking id");
        Assert.assertEquals(response.jsonPath().getObject("booking", Booking.class), newBooking);
        booking = newBooking;
    }

    @Test(groups = {"api", "smoke"}, dependsOnMethods = "createBooking",
            description = "GET /booking/{id} returns the created booking")
    public void getBookingById() {
        Response response = ApiClient.request(BASE_URI).get("/booking/" + bookingId);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.as(Booking.class), booking);
    }

    @Test(groups = {"api", "regression"}, dependsOnMethods = "createBooking",
            description = "GET /booking?firstname=&lastname= finds the created booking")
    public void searchBookingByName() {
        Response response = ApiClient.request(BASE_URI)
                .queryParam("firstname", booking.firstname())
                .queryParam("lastname", booking.lastname())
                .get("/booking");

        Assert.assertEquals(response.getStatusCode(), 200);
        List<Integer> ids = response.jsonPath().getList("bookingid", Integer.class);
        Assert.assertTrue(ids.contains(bookingId), "Booking " + bookingId + " not found in " + ids);
    }

    @Test(groups = {"api", "regression"}, dependsOnMethods = "createBooking",
            description = "PUT /booking/{id} without a token is forbidden")
    public void updateBookingWithoutTokenIsForbidden() {
        Response response = ApiClient.request(BASE_URI).body(booking).put("/booking/" + bookingId);

        Assert.assertEquals(response.getStatusCode(), 403);
    }

    @Test(groups = {"api", "regression"}, dependsOnMethods = {"createAuthToken", "getBookingById"},
            description = "PUT /booking/{id} replaces the whole booking")
    public void updateBooking() {
        Booking updated = new Booking("Api", booking.lastname(), 300, false,
                new BookingDates("2026-12-10", "2026-12-15"), "Late checkout");

        Response response = ApiClient.request(BASE_URI)
                .cookie("token", token)
                .body(updated)
                .put("/booking/" + bookingId);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.as(Booking.class), updated);
        booking = updated;
    }

    @Test(groups = {"api", "regression"}, dependsOnMethods = "updateBooking",
            description = "PATCH /booking/{id} changes only the fields sent")
    public void partiallyUpdateBooking() {
        Response response = ApiClient.request(BASE_URI)
                .cookie("token", token)
                .body(Map.of("firstname", "Patched", "totalprice", 999))
                .patch("/booking/" + bookingId);

        Assert.assertEquals(response.getStatusCode(), 200);
        Booking patched = response.as(Booking.class);
        Assert.assertEquals(patched.firstname(), "Patched");
        Assert.assertEquals(patched.totalprice(), Integer.valueOf(999));
        Assert.assertEquals(patched.lastname(), booking.lastname(), "Fields not sent should keep their value");
        Assert.assertEquals(patched.bookingdates(), booking.bookingdates(), "Fields not sent should keep their value");
        booking = patched;
    }

    @Test(groups = {"api", "smoke"},
            dependsOnMethods = {"partiallyUpdateBooking", "searchBookingByName", "updateBookingWithoutTokenIsForbidden"},
            description = "DELETE /booking/{id} removes the booking")
    public void deleteBooking() {
        Response response = ApiClient.request(BASE_URI)
                .cookie("token", token)
                .delete("/booking/" + bookingId);

        // Restful Booker answers 201 Created for a successful delete
        Assert.assertEquals(response.getStatusCode(), 201);

        Response afterDelete = ApiClient.request(BASE_URI).get("/booking/" + bookingId);
        Assert.assertEquals(afterDelete.getStatusCode(), 404, "Booking should be gone after delete");
    }
}
