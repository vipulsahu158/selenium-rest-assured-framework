package com.framework.api;

import com.framework.config.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper over Rest Assured. A fresh request specification is built for every call,
 * so nothing is shared between threads and parallel API tests are safe.
 */
public final class ApiClient {

    private ApiClient() {
    }

    /** Request for the default API (api.base.url). */
    public static RequestSpecification request() {
        return request(ConfigReader.get("api.base.url"));
    }

    /**
     * Request for any base URI. Add headers, query params or a body (String, Map or POJO)
     * and finish with get/post/put/patch/delete, e.g. {@code request(url).body(booking).post("/booking")}.
     */
    public static RequestSpecification request(String baseUri) {
        return given().spec(new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                // plain "application/json": ContentType.JSON sends four media types, which some APIs reject (418)
                .setAccept("application/json")
                .addFilter(new ExtentRestAssuredFilter())
                .build());
    }

    public static Response send(String method, String endpoint, String body) {
        RequestSpecification request = request();
        if (body != null && !body.isBlank()) {
            request.body(body);
        }
        switch (method.toUpperCase()) {
            case "GET":
                return request.get(endpoint);
            case "POST":
                return request.post(endpoint);
            case "PUT":
                return request.put(endpoint);
            case "PATCH":
                return request.patch(endpoint);
            case "DELETE":
                return request.delete(endpoint);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    public static Response get(String endpoint) {
        return send("GET", endpoint, null);
    }

    public static Response post(String endpoint, String body) {
        return send("POST", endpoint, body);
    }

    public static Response put(String endpoint, String body) {
        return send("PUT", endpoint, body);
    }

    public static Response patch(String endpoint, String body) {
        return send("PATCH", endpoint, body);
    }

    public static Response delete(String endpoint) {
        return send("DELETE", endpoint, null);
    }
}
