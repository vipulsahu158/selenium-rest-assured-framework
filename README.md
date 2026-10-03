# Selenium + Rest Assured Automation Framework

Java 17 · Maven · TestNG · Selenium 4 · Rest Assured · Apache POI · ExtentReports

UI tests run against https://www.saucedemo.com. API tests run against two free public APIs:
https://jsonplaceholder.typicode.com (fake writes) and https://restful-booker.herokuapp.com (really stores data). Change the URLs in `src/main/resources/config.properties` to point at your own app.

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
  api/models/Post, Booking, BookingDates   request/response bodies as Java records (Jackson)
  utils/ExcelUtils           sheet -> List<Map<header,value>>
  utils/ScreenshotUtils      base64 + PNG file
  listeners/TestListener     report entries, screenshot on failure
  reports/ExtentManager, ExtentTestManager
src/test/java/com/framework
  dataproviders/DataProviders   one provider per Excel sheet
  tests/ui/BaseTest, LoginTests, CartTests
  tests/api/PostsApiTests        JSONPlaceholder: GET (list, by id, query param, nested, 404), POST, PUT, PATCH, DELETE
  tests/api/BookingApiTests      Restful Booker end-to-end CRUD: auth token -> create -> read -> search -> PUT -> PATCH -> DELETE
  tests/api/ExcelDrivenApiTests  one test per row of the ApiData sheet
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

## API tests

| Class | API | Covers |
|---|---|---|
| `PostsApiTests` | JSONPlaceholder | GET all / by id / `?userId=` / `/posts/1/comments` / 404, POST, PUT, PATCH, DELETE. Writes are faked by the server, so each test checks its own response |
| `BookingApiTests` | Restful Booker | `POST /auth` token (+ bad credentials), POST, GET, GET search, PUT, PATCH, DELETE then GET 404, PUT without token returns 403. Tests are chained with `dependsOnMethods` and share the created booking |
| `ExcelDrivenApiTests` | JSONPlaceholder | Rows of the `ApiData` sheet |

Write a new API test with `ApiClient.request()` (default API) or `ApiClient.request(baseUri)` and the usual
Rest Assured calls, e.g. `ApiClient.request(url).cookie("token", token).body(booking).put("/booking/" + id)`.
Bodies can be JSON strings, `Map`s or POJOs/records. Restful Booker quirks the tests account for: a successful
DELETE returns `201`, bad credentials return `200` with `{"reason":"Bad credentials"}`.

## Extending
- New page: extend `BasePage`, add a test class that extends `BaseTest`.
- New data-driven test: add a sheet, a provider in `DataProviders`, and a test taking `Map<String,String>`.
- New browser: add a `case` in `DriverFactory` and a `<test>` block in `testng.xml`.
- Demo a failure screenshot: edit an `ExpectedMessage` in `LoginData` so it no longer matches and re-run.

## Tuning parallelism
`testng.xml` opens up to (browsers x `data-provider-thread-count`) windows at once. Lower
`data-provider-thread-count` on small machines, or raise it when using a grid.
