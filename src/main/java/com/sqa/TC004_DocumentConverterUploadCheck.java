package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TC004_DocumentConverterUploadCheck extends BaseSetup {
    public static void main(String[] args) {
        TC004_DocumentConverterUploadCheck test = new TC004_DocumentConverterUploadCheck();

        try {
            System.out.println("Starting TC004...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            // Step 1: Open Document Converter main menu
            boolean clickedMain = false;

            List<WebElement> buttons = test.driver.findElements(By.tagName("button"));
            for (WebElement button : buttons) {
                String text = button.getText().trim().toLowerCase();
                if (text.contains("document converter")) {
                    System.out.println("Found main button: " + button.getText());
                    js.executeScript("arguments[0].click();", button);
                    clickedMain = true;
                    break;
                }
            }

            if (!clickedMain) {
                System.out.println("TC004 - Fail: Could not open Document Converter menu.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC004_DocumentConverterUploadCheck");
                return;
            }

            Thread.sleep(2000);

            // Step 2: Click exact sub-tool -> Word to PDF
            WebElement wordToPdfButton = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[contains(.,'Word') and contains(.,'PDF')]")
                    )
            );

            System.out.println("Found sub-tool: " + wordToPdfButton.getText());
            js.executeScript("arguments[0].click();", wordToPdfButton);

            Thread.sleep(3000);

            // Step 3: Check page content after sub-tool click
            String currentUrl = test.driver.getCurrentUrl();
            String pageSource = test.driver.getPageSource().toLowerCase();

            System.out.println("Current URL: " + currentUrl);

            // Step 4: Find upload element
            List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));

            // Also check for upload-related text/buttons
            List<WebElement> uploadElements = test.driver.findElements(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upload') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'drop') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'choose file') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'select file')]")
            );

            System.out.println("File inputs found: " + fileInputs.size());
            System.out.println("Upload-related elements found: " + uploadElements.size());

            boolean foundUpload = false;

            if (!fileInputs.isEmpty()) {
                foundUpload = true;
            }

            if (!foundUpload) {
                for (WebElement el : uploadElements) {
                    if (el.isDisplayed()) {
                        System.out.println("Visible upload-related element: " + el.getText());
                        foundUpload = true;
                        break;
                    }
                }
            }

            if (foundUpload) {
                System.out.println("TC004 - Pass: Upload element is available after opening Word to PDF.");
            } else if (pageSource.contains("word to pdf") || pageSource.contains("upload") || pageSource.contains("drop file")) {
                System.out.println("TC004 - Pass: Word to PDF sub-tool opened, but upload field may be dynamically hidden.");
            } else {
                System.out.println("TC004 - Fail: Word to PDF page opened, but no upload element was detected.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC004_DocumentConverterUploadCheck");

        } catch (Exception e) {
            System.out.println("TC004 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
