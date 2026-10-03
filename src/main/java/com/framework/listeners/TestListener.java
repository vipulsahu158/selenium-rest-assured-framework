package com.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.framework.config.ConfigReader;
import com.framework.driver.DriverManager;
import com.framework.reports.ExtentManager;
import com.framework.reports.ExtentTestManager;
import com.framework.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.ITestListener;

import java.util.Map;

/**
 * Drives the HTML report and captures a screenshot whenever a UI test fails.
 * onTestFailure runs BEFORE @AfterMethod, so the browser is still open at that point.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String browser = result.getTestContext().getCurrentXmlTest().getParameter("browser");
        boolean isApi = result.getTestClass().getName().contains(".api.");
        String device = isApi ? "API" : (browser != null ? browser : ConfigReader.get("browser", "chrome"));

        String name = result.getMethod().getMethodName() + dataLabel(result) + " [" + device + "]";
        ExtentTest test = ExtentManager.getInstance().createTest(name, result.getMethod().getDescription());
        test.assignCategory(isApi ? "API" : "UI");
        test.assignDevice(device);
        for (String group : result.getMethod().getGroups()) {
            test.assignCategory(group);
        }
        ExtentTestManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().pass(MarkupHelper.createLabel("TEST PASSED", ExtentColor.GREEN));
        ExtentTestManager.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        WebDriver driver = DriverManager.getDriver();
        if (driver != null) {
            try {
                String base64 = ScreenshotUtils.asBase64(driver);
                String file = ScreenshotUtils.saveToFile(driver, result.getMethod().getMethodName());
                test.fail(result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure screenshot").build());
                test.info("Screenshot saved: " + file);
            } catch (Exception e) {
                test.fail(result.getThrowable());
                test.warning("Could not capture screenshot: " + e.getMessage());
            }
        } else {
            test.fail(result.getThrowable());
        }
        ExtentTestManager.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();
        if (test == null) {
            test = ExtentManager.getInstance().createTest(result.getMethod().getMethodName());
        }
        test.skip(result.getThrowable() != null ? result.getThrowable().toString() : "Test skipped");
        ExtentTestManager.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        System.out.println("HTML report: " + ExtentManager.getReportPath());
    }

    /** Builds " - TC01 - description" from the first data-provider row, if any. */
    private String dataLabel(ITestResult result) {
        Object[] params = result.getParameters();
        if (params != null && params.length > 0 && params[0] instanceof Map) {
            Map<?, ?> row = (Map<?, ?>) params[0];
            Object id = row.get("TestCaseId");
            Object desc = row.get("Description");
            StringBuilder sb = new StringBuilder();
            if (id != null) {
                sb.append(" - ").append(id);
            }
            if (desc != null && !desc.toString().isEmpty()) {
                sb.append(" - ").append(desc);
            }
            return sb.toString();
        }
        return "";
    }
}
