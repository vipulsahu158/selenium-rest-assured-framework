package com.framework.tests.ui.practice;

import com.framework.driver.DriverManager;
import com.framework.pages.practice.HomePage;
import com.framework.pages.practice.ShadowDomPage;
import com.framework.pages.practice.TablesPage;
import com.framework.tests.ui.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Reading structured data (tables, shadow DOM) and moving between pages. */
public class TablesAndNavigationTests extends BaseTest {

    @Test(groups = {"practice", "smoke"}, description = "Home page lists the examples and a link navigates to one")
    public void homePageNavigation() {
        HomePage home = new HomePage(DriverManager.getDriver()).open();

        Assert.assertEquals(home.getHeading(), "Welcome to the-internet");
        Assert.assertTrue(home.getExampleCount() >= 30, "Examples listed: " + home.getExampleCount());

        home.openExample("Checkboxes");
        Assert.assertTrue(home.getCurrentUrl().endsWith("/checkboxes"), "URL: " + home.getCurrentUrl());

        DriverManager.getDriver().navigate().back();
        Assert.assertEquals(home.getHeading(), "Welcome to the-internet");
    }

    @Test(groups = {"practice", "regression"}, description = "Table cells are read by column and by row key")
    public void readTable() {
        TablesPage page = new TablesPage(DriverManager.getDriver()).open();

        Assert.assertEquals(page.getRowCount(), 4);
        Assert.assertEquals(page.getColumn("Last Name"), List.of("Smith", "Bach", "Doe", "Conway"));
        Assert.assertEquals(page.getCell("Email", "jdoe@hotmail.com", "First Name"), "Jason");
        Assert.assertEquals(page.getCell("Last Name", "Bach", "Due"), "$51.00");
    }

    @Test(groups = {"practice", "regression"}, description = "Clicking a column header sorts the table")
    public void sortTable() {
        TablesPage page = new TablesPage(DriverManager.getDriver()).open();

        page.sortBy("Last Name");
        List<String> names = page.getColumn("Last Name");
        List<String> expectedNames = new ArrayList<>(names);
        expectedNames.sort(Comparator.naturalOrder());
        Assert.assertEquals(names, expectedNames, "Last Name should be ascending");

        page.sortBy("Due");
        List<Double> dues = new ArrayList<>();
        for (String due : page.getColumn("Due")) {
            dues.add(Double.parseDouble(due.replace("$", "")));
        }
        List<Double> expectedDues = new ArrayList<>(dues);
        expectedDues.sort(Comparator.naturalOrder());
        Assert.assertEquals(dues, expectedDues, "Due should be ascending");
    }

    @Test(groups = {"practice", "regression"}, description = "Text inside a shadow root is reached with getShadowRoot()")
    public void shadowDom() {
        ShadowDomPage page = new ShadowDomPage(DriverManager.getDriver()).open();

        Assert.assertTrue(page.isShadowParagraphHiddenFromNormalLookup(),
                "A plain CSS lookup must not see inside the shadow root");
        Assert.assertFalse(page.getShadowParagraphText().isEmpty(), "Shadow paragraph should have text");
    }
}
