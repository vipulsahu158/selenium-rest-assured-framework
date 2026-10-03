package com.framework.tests.ui.practice;

import com.framework.driver.DriverManager;
import com.framework.pages.practice.DragAndDropPage;
import com.framework.pages.practice.HoversPage;
import com.framework.tests.ui.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/** Actions that need the mouse: hover and drag and drop. */
public class MouseActionTests extends BaseTest {

    @Test(groups = {"practice", "regression"}, description = "Hovering over each image reveals that user's caption")
    public void hoverShowsCaption() {
        HoversPage page = new HoversPage(DriverManager.getDriver()).open();
        Assert.assertEquals(page.getFigureCount(), 3);

        for (int i = 0; i < page.getFigureCount(); i++) {
            Assert.assertFalse(page.isCaptionDisplayed(i), "Caption " + (i + 1) + " should be hidden before hover");
            page.hoverOverFigure(i);
            Assert.assertTrue(page.isCaptionDisplayed(i), "Caption " + (i + 1) + " should show on hover");
            Assert.assertEquals(page.getCaptionName(i), "name: user" + (i + 1));
        }
    }

    @Test(groups = {"practice", "regression"}, description = "Dragging box A onto box B swaps them")
    public void dragAndDrop() {
        DragAndDropPage page = new DragAndDropPage(DriverManager.getDriver()).open();
        Assert.assertEquals(page.getBoxATitle(), "A");
        Assert.assertEquals(page.getBoxBTitle(), "B");

        page.dragAOntoB();

        Assert.assertEquals(page.getBoxATitle(), "B");
        Assert.assertEquals(page.getBoxBTitle(), "A");
    }
}
