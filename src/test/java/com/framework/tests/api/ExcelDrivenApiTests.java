package com.framework.tests.api;

import com.framework.api.ApiClient;
import com.framework.dataproviders.DataProviders;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/** Runs every row of the ApiData sheet; add a row to add an API test without writing code. */
public class ExcelDrivenApiTests {

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
}
