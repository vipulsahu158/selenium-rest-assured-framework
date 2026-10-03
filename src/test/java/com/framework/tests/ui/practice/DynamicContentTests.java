package com.framework.tests.ui.practice;

import com.framework.driver.DriverManager;
import com.framework.pages.practice.AddRemoveElementsPage;
import com.framework.pages.practice.DynamicControlsPage;
import com.framework.pages.practice.DynamicLoadingPage;
import com.framework.pages.practice.InfiniteScrollPage;
import com.framework.tests.ui.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Content that appears, changes or disappears after the page loads, so every check needs a wait. */
public class DynamicContentTests extends BaseTest {

    @Test(groups = {"practice", "smoke"}, description = "Hidden element becomes visible after loading")
    public void dynamicLoadingHiddenElement() {
        DynamicLoadingPage page = new DynamicLoadingPage(DriverManager.getDriver()).open(1);
        Assert.assertTrue(page.isResultInDom(), "Example 1 has the result in the DOM from the start");

        page.start();

        Assert.assertEquals(page.getResultText(), "Hello World!");
        Assert.assertFalse(page.isLoadingShown(), "The loader should be gone");
    }

    @Test(groups = {"practice", "regression"}, description = "Element is added to the DOM after loading")
    public void dynamicLoadingElementRenderedAfterwards() {
        DynamicLoadingPage page = new DynamicLoadingPage(DriverManager.getDriver()).open(2);
        Assert.assertFalse(page.isResultInDom(), "Example 2 has no result in the DOM before Start");

        page.start();

        Assert.assertEquals(page.getResultText(), "Hello World!");
    }

    @Test(groups = {"practice", "regression"}, description = "Checkbox is removed and added back through Ajax")
    public void removeAndAddCheckbox() {
        DynamicControlsPage page = new DynamicControlsPage(DriverManager.getDriver()).open();
        Assert.assertTrue(page.isCheckboxPresent());

        page.removeCheckbox();
        Assert.assertFalse(page.isCheckboxPresent(), "Checkbox should be removed");

        page.addCheckbox();
        Assert.assertTrue(page.isCheckboxPresent(), "Checkbox should be back");
    }

    @Test(groups = {"practice", "regression"}, description = "Text box is enabled, used, then disabled")
    public void enableAndDisableTextBox() {
        DynamicControlsPage page = new DynamicControlsPage(DriverManager.getDriver()).open();
        Assert.assertFalse(page.isInputEnabled(), "Text box starts disabled");

        page.enableInput();
        Assert.assertTrue(page.isInputEnabled());
        page.typeInInput("now it works");
        Assert.assertEquals(page.getInputValue(), "now it works");

        page.disableInput();
        Assert.assertFalse(page.isInputEnabled(), "Text box should be disabled again");
    }

    @Test(groups = {"practice", "regression"}, description = "Elements are added and removed one by one")
    public void addAndRemoveElements() {
        AddRemoveElementsPage page = new AddRemoveElementsPage(DriverManager.getDriver()).open();
        Assert.assertEquals(page.getDeleteButtonCount(), 0);

        page.addElement();
        page.addElement();
        page.addElement();
        Assert.assertEquals(page.getDeleteButtonCount(), 3);

        page.deleteFirstElement();
        Assert.assertEquals(page.getDeleteButtonCount(), 2);
    }

    @Test(groups = {"practice", "regression"}, description = "More content loads when scrolling to the bottom")
    public void infiniteScroll() {
        InfiniteScrollPage page = new InfiniteScrollPage(DriverManager.getDriver()).open();
        int before = page.getParagraphCount();

        page.scrollUntilMoreLoaded(before);

        Assert.assertTrue(page.getParagraphCount() > before, "Scrolling should load more paragraphs");
    }
}
