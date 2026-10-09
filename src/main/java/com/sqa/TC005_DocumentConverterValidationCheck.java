package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TC005_DocumentConverterValidationCheck extends BaseSetup {
    public static void main(String[] args) {
        TC005_DocumentConverterValidationCheck test = new TC005_DocumentConverterValidationCheck();

        try {
            System.out.println("Starting TC005...");
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
                System.out.println("TC005 - Fail: Could not open Document Converter menu.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC005_DocumentConverterValidationCheck");
                return;
            }

            Thread.sleep(2000);

            // Step 2: Open exact sub-tool -> Word to PDF
            WebElement wordToPdfButton = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[contains(.,'Word') and contains(.,'PDF')]")
                    )
            );

            System.out.println("Found sub-tool: " + wordToPdfButton.getText());
            js.executeScript("arguments[0].click();", wordToPdfButton);

            Thread.sleep(3000);

            // Step 3: Look only for actual conversion action buttons
            List<WebElement> actionButtons = test.driver.findElements(
                    By.xpath("//button[normalize-space()='Convert' or normalize-space()='Start Conversion' or normalize-space()='Upload and Convert']")
            );

            int visibleActionButtons = 0;

            for (WebElement btn : actionButtons) {
                if (btn.isDisplayed()) {
                    visibleActionButtons++;
                    System.out.println("Visible action button found: " + btn.getText());
                }
            }

            System.out.println("Total action buttons found: " + actionButtons.size());
            System.out.println("Visible action buttons found: " + visibleActionButtons);

            if (visibleActionButtons == 0) {
                System.out.println("TC005 - Pass: Actual convert action is not available before file upload.");
            } else {
                System.out.println("TC005 - Fail: Actual convert action is visible before uploading a file.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC005_DocumentConverterValidationCheck");

        } catch (Exception e) {
            System.out.println("TC005 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
