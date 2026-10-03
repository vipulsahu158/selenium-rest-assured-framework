package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InfiniteScrollPage extends PracticePage {

    private static final By PARAGRAPHS = By.cssSelector(".jscroll-added");

    public InfiniteScrollPage(WebDriver driver) {
        super(driver);
    }

    public InfiniteScrollPage open() {
        openPath("/infinite_scroll");
        waitVisible(PARAGRAPHS);
        return this;
    }

    public int getParagraphCount() {
        return count(PARAGRAPHS);
    }

    /** Scrolls down until more paragraphs than {@code before} have been loaded. */
    public void scrollUntilMoreLoaded(int before) {
        wait.until(d -> {
            scrollToBottom();
            return count(PARAGRAPHS) > before;
        });
    }
}
