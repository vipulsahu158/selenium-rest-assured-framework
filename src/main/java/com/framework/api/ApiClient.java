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

    private static RequestSpecification baseSpec() {
        return given().spec(new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new ExtentRestAssuredFilter())
                .build());
    }

    public static Response send(String method, String endpoint, String body) {
        RequestSpecification request = baseSpec();
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

    public static Response delete(String endpoint) {
        return send("DELETE", endpoint, null);
    }
}
