package com.framework.tests.api;

import com.framework.api.ApiClient;
import com.framework.dataproviders.DataProviders;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public class PostsApiTests {

    /** One generic, Excel-driven API test: method, endpoint, body, expected status and one JSON field check. */
    @Test(dataProvider = "apiData", dataProviderClass = DataProviders.class, groups = {"api", "regression"})
    public void verifyApi(Map<String, String> data) {
        Response response = ApiClient.send(data.get("Method"), data.get("Endpoint"), data.get("RequestBody"));

        Assert.assertEquals(response.getStatusCode(), Integer.parseInt(data.get("ExpectedStatus")),
                "Unexpected HTTP status");

        String field = data.get("ExpectedField");
        if (field != null && !field.isEmpty()) {
            Assert.assertEquals(response.jsonPath().getString(field), data.get("ExpectedValue"),
                    "Unexpected value for JSON field '" + field + "'");
        }
    }

    /** A hand-written API test showing the client can also be used without Excel. */
    @Test(groups = {"api", "smoke"}, description = "GET /posts returns a non-empty list quickly")
    public void verifyPostsListAndResponseTime() {
        Response response = ApiClient.get("/posts");
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.jsonPath().getList("$").size() > 0, "Expected at least one post");
        Assert.assertTrue(response.getTime() < 5000, "Response took " + response.getTime() + " ms");
    }
}
