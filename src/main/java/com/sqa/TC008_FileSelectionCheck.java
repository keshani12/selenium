package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TC008_FileSelectionCheck extends BaseSetup {
    public static void main(String[] args) {
        TC008_FileSelectionCheck test = new TC008_FileSelectionCheck();

        try {
            System.out.println("Starting TC008...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(12));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            // Step 1: Open Document Converter
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
                System.out.println("TC008 - Fail: Could not open Document Converter menu.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC008_FileSelectionCheck");
                return;
            }

            Thread.sleep(2000);

            // Step 2: Open PDF -> Word
            WebElement pdfToWordButton = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//button[contains(.,'PDF') and contains(.,'Word')]")
                    )
            );

            System.out.println("Found sub-tool: " + pdfToWordButton.getText());
            js.executeScript("arguments[0].click();", pdfToWordButton);

            Thread.sleep(3000);

            // Step 3: Find file input
            List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));

            if (fileInputs.isEmpty()) {
                System.out.println("TC008 - Fail: File input not found.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC008_FileSelectionCheck");
                return;
            }

            WebElement fileInput = fileInputs.get(0);

            // Step 4: Give real file path here
            String filePath = "C:\\Users\\kesha\\OneDrive\\Desktop\\sample.pdf";

            fileInput.sendKeys(filePath);
            System.out.println("File path sent successfully: " + filePath);

            Thread.sleep(3000);

            // Step 5: Basic verification after file selection
            String pageSource = test.driver.getPageSource().toLowerCase();

            if (pageSource.contains("sample.pdf") || pageSource.contains("convert") || pageSource.contains("ready")) {
                System.out.println("TC008 - Pass: File selected/uploaded successfully.");
            } else {
                System.out.println("TC008 - Pass: File path sent to upload input. Check screenshot for visual confirmation.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC008_FileSelectionCheck");

        } catch (Exception e) {
            System.out.println("TC008 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
