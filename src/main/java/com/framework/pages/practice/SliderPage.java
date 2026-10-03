package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SliderPage extends PracticePage {

    private static final By SLIDER = By.cssSelector(".sliderContainer input[type='range']");
    private static final By VALUE = By.id("range");

    public SliderPage(WebDriver driver) {
        super(driver);
    }

    public SliderPage open() {
        openPath("/horizontal_slider");
        waitVisible(SLIDER);
        return this;
    }

    /**
     * Sets the slider (0 to 5, steps of 0.5). The page updates its label from the "change" event, which
     * a script-set value does not fire by itself, so the event is dispatched explicitly.
     */
    public void setValue(double value) {
        ExtentTestManager.log("Set the slider to " + value);
        js("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                waitVisible(SLIDER), value);
    }

    public String getSliderPosition() {
        return waitVisible(SLIDER).getAttribute("value");
    }

    public String getValue() {
        return getText(VALUE);
    }
}
