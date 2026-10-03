package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class DropdownPage extends PracticePage {

    private static final By DROPDOWN = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        super(driver);
    }

    public DropdownPage open() {
        openPath("/dropdown");
        waitVisible(DROPDOWN);
        return this;
    }

    private Select select() {
        return new Select(waitVisible(DROPDOWN));
    }

    public void selectByText(String text) {
        ExtentTestManager.log("Select '" + text + "' in the dropdown");
        select().selectByVisibleText(text);
    }

    public String getSelectedText() {
        return select().getFirstSelectedOption().getText().trim();
    }

    public List<String> getOptions() {
        return select().getOptions().stream().map(o -> o.getText().trim()).collect(Collectors.toList());
    }
}
