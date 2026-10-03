package com.framework.pages.practice;

import com.framework.reports.ExtentTestManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.nio.file.Path;

public class FileUploadPage extends PracticePage {

    private static final By FILE_INPUT = By.id("file-upload");
    private static final By UPLOAD_BUTTON = By.id("file-submit");
    private static final By HEADING = By.tagName("h3");
    private static final By UPLOADED_NAME = By.id("uploaded-files");

    public FileUploadPage(WebDriver driver) {
        super(driver);
    }

    public FileUploadPage open() {
        openPath("/upload");
        waitVisible(UPLOAD_BUTTON);
        return this;
    }

    public void upload(Path file) {
        ExtentTestManager.log("Choose file " + file.getFileName());
        // a file input takes the path as text; no need to open the OS file dialog
        driver.findElement(FILE_INPUT).sendKeys(file.toAbsolutePath().toString());
        click(UPLOAD_BUTTON, "Upload button");
        // the upload form posts to a new page; wait for it so the heading is not read from the old page
        waitVisible(UPLOADED_NAME);
    }

    public String getHeading() {
        return getText(HEADING);
    }

    public String getUploadedFileName() {
        return getText(UPLOADED_NAME);
    }
}
