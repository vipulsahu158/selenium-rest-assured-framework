# Selenium + Rest Assured Automation Framework

Java 17 · Maven · TestNG · Selenium 4 · Rest Assured · Apache POI · ExtentReports

UI tests run against https://www.saucedemo.com and API tests against https://jsonplaceholder.typicode.com
(both public demo sites). Change the URLs in `src/main/resources/config.properties` to point at your own app.

## Requirements
- JDK 17+ and Maven 3.8+
- Chrome, Firefox and/or Edge installed (Selenium Manager downloads the matching drivers automatically)

## Run

```bash
mvn clean test                                          # full suite: Chrome + Firefox + Edge + API, in parallel
mvn clean test -Dheadless=true                          # same, headless (good for CI)
mvn clean test -DsuiteXmlFile=testng-api.xml            # API tests only (no browser needed)
mvn clean test -DsuiteXmlFile=testng-ui-chrome.xml      # UI tests on Chrome only
mvn clean test -Dgroups=smoke                           # run one TestNG group
mvn clean test -Dgrid.url=http://localhost:4444         # run browsers on Selenium Grid / Selenoid / cloud
```

Open the report at `reports/ExecutionReport_<timestamp>.html` (the path is printed at the end of the run).
Failure screenshots are embedded in the report and also saved in `reports/screenshots/`.

## Structure

```
src/main/java/com/framework
  config/ConfigReader        config.properties + -D overrides
  driver/DriverFactory       chrome / firefox / edge / remote grid
  driver/DriverManager       ThreadLocal<WebDriver> (parallel safe)
  pages/BasePage             waits + common actions
  pages/LoginPage, InventoryPage      page objects (one class per page)
  api/ApiClient              Rest Assured wrapper (GET/POST/PUT/PATCH/DELETE)
  api/ExtentRestAssuredFilter  logs every request/response into the report
  utils/ExcelUtils           sheet -> List<Map<header,value>>
  utils/ScreenshotUtils      base64 + PNG file
  listeners/TestListener     report entries, screenshot on failure
  reports/ExtentManager, ExtentTestManager
src/test/java/com/framework
  dataproviders/DataProviders   one provider per Excel sheet
  tests/ui/BaseTest, LoginTests, CartTests
  tests/api/PostsApiTests
src/test/resources/testdata/TestData.xlsx    LoginData, CartData, ApiData sheets
testng.xml                   cross-browser parallel suite
```

## How each requirement is met

| Requirement | Implementation |
|---|---|
| Page Object Model | Locators and actions live in `pages/*`; tests only call page methods and assert |
| Parallel execution | `parallel="tests"` runs each browser block concurrently; `@DataProvider(parallel = true)` + `data-provider-thread-count` runs Excel rows concurrently. WebDriver and ExtentTest are `ThreadLocal`, Rest Assured specs are built per call |
| HTML report | ExtentReports Spark report, one timestamped file per run, with request/response bodies for API tests |
| Data driven | `ExcelUtils` reads `TestData.xlsx`; each row becomes a `Map<String,String>` test input |
| Screenshot on failure | `TestListener.onTestFailure` takes the screenshot while the browser is still open, attaches it to the report and saves a PNG |
| Cross-browser | `browser` parameter per `<test>` in `testng.xml`; add or remove blocks to change the matrix |

## Excel conventions
- Row 1 = headers, each following row = one test-data set. Add a row to add a test case.
- `Run` column: set to `N` to skip a row.
- Sheet names: `LoginData`, `CartData`, `ApiData` (see `DataProviders`).
- API rows: `Method, Endpoint, RequestBody, ExpectedStatus, ExpectedField (JSON path), ExpectedValue`.

## Extending
- New page: extend `BasePage`, add a test class that extends `BaseTest`.
- New data-driven test: add a sheet, a provider in `DataProviders`, and a test taking `Map<String,String>`.
- New browser: add a `case` in `DriverFactory` and a `<test>` block in `testng.xml`.
- Demo a failure screenshot: edit an `ExpectedMessage` in `LoginData` so it no longer matches and re-run.

## Tuning parallelism
`testng.xml` opens up to (browsers x `data-provider-thread-count`) windows at once. Lower
`data-provider-thread-count` on small machines, or raise it when using a grid.
