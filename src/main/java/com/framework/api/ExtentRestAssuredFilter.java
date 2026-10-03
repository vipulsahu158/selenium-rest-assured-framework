package com.framework.api;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.framework.reports.ExtentTestManager;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * Writes every API request and response into the HTML report of the running test.
 */
public class ExtentRestAssuredFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        Response response = ctx.next(request, responseSpec);

        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info("Request: " + request.getMethod() + " " + request.getURI());
            if (request.getBody() != null) {
                test.info(MarkupHelper.createCodeBlock(String.valueOf(request.getBody()), CodeLanguage.JSON));
            }
            test.info("Response: " + response.getStatusLine() + " (" + response.getTime() + " ms)");
            String body = response.getBody().asString();
            if (body != null && !body.isBlank()) {
                test.info(MarkupHelper.createCodeBlock(body, CodeLanguage.JSON));
            }
        }
        return response;
    }
}
