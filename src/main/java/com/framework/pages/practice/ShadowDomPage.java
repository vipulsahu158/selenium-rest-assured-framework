package com.framework.pages.practice;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/** Content inside a shadow root is invisible to normal locators; getShadowRoot() opens it. */
public class ShadowDomPage extends PracticePage {

    private static final By HOSTS = By.tagName("my-paragraph");

    public ShadowDomPage(WebDriver driver) {
        super(driver);
    }

    public ShadowDomPage open() {
        openPath("/shadowdom");
        waitVisible(HOSTS);
        return this;
    }

    /** Text of the <p> that lives inside the shadow root of the first custom element. */
    public String getShadowParagraphText() {
        SearchContext shadow = driver.findElements(HOSTS).get(0).getShadowRoot();
        return shadow.findElement(By.cssSelector("p")).getText().trim();
    }

    /** True when an ordinary lookup cannot see the shadow content. */
    public boolean isShadowParagraphHiddenFromNormalLookup() {
        return driver.findElements(By.cssSelector("my-paragraph p")).isEmpty();
    }

    public List<String> getLightDomTexts() {
        return driver.findElements(By.cssSelector("my-paragraph > *")).stream()
                .map(WebElement::getText).map(String::trim).collect(Collectors.toList());
    }
}
