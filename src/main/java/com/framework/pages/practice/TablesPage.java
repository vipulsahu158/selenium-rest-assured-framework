package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class TablesPage extends PracticePage {

    private static final By HEADERS = By.cssSelector("#table1 thead th");
    private static final By ROWS = By.cssSelector("#table1 tbody tr");

    public TablesPage(WebDriver driver) {
        super(driver);
    }

    public TablesPage open() {
        openPath("/tables");
        waitVisible(HEADERS);
        return this;
    }

    public int getRowCount() {
        return count(ROWS);
    }

    /** 1-based position of the column with this header text. */
    private int columnIndex(String header) {
        List<WebElement> headers = driver.findElements(HEADERS);
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().equals(header)) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("No column named '" + header + "'");
    }

    public List<String> getColumn(String header) {
        By cells = By.cssSelector("#table1 tbody tr td:nth-of-type(" + columnIndex(header) + ")");
        return driver.findElements(cells).stream().map(e -> e.getText().trim()).collect(Collectors.toList());
    }

    /**
     * Clicks the header and waits until the column's order has actually changed. On a busy machine the
     * click can land before the layout settles and do nothing, so it is repeated (up to 3 times) when the
     * order has not changed after 5 seconds.
     */
    public void sortBy(String header) {
        List<String> before = getColumn(header);
        By headerCell = By.cssSelector("#table1 thead th:nth-of-type(" + columnIndex(header) + ")");
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        for (int attempt = 1; ; attempt++) {
            click(headerCell, "'" + header + "' column header");
            try {
                shortWait.until(d -> !getColumn(header).equals(before));
                return;
            } catch (TimeoutException e) {
                if (attempt == 3) {
                    throw e;
                }
            }
        }
    }

    /** Value of the cell in the row where {@code keyHeader} equals {@code keyValue}, under {@code header}. */
    public String getCell(String keyHeader, String keyValue, String header) {
        int row = getColumn(keyHeader).indexOf(keyValue);
        if (row < 0) {
            throw new IllegalArgumentException("No row with " + keyHeader + " = " + keyValue);
        }
        return getColumn(header).get(row);
    }
}
