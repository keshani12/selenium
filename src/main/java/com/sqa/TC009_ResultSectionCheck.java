package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TC009_ResultSectionCheck extends BaseSetup {
    public static void main(String[] args) {
        TC009_ResultSectionCheck test = new TC009_ResultSectionCheck();

        try {
            System.out.println("Starting TC009...");
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
                System.out.println("TC009 - Fail: Could not open Document Converter menu.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC009_ResultSectionCheck");
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

            // Step 3: Upload file
            List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));

            if (fileInputs.isEmpty()) {
                System.out.println("TC009 - Fail: File input not found.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC009_ResultSectionCheck");
                return;
            }

            WebElement fileInput = fileInputs.get(0);

            // CHANGE THIS TO YOUR REAL FILE PATH
            String filePath = "C:\\Users\\kesha\\OneDrive\\Desktop\\sample.pdf";

            fileInput.sendKeys(filePath);
            System.out.println("File path sent successfully: " + filePath);

            Thread.sleep(4000);

            // Step 4: Check whether convert/result/download area appears
            List<WebElement> convertButtons = test.driver.findElements(
                    By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'convert')]")
            );

            List<WebElement> downloadElements = test.driver.findElements(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'ready') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'result')]")
            );

            int visibleConvertButtons = 0;
            int visibleDownloadElements = 0;

            for (WebElement btn : convertButtons) {
                if (btn.isDisplayed()) {
                    visibleConvertButtons++;
                    System.out.println("Visible convert button: " + btn.getText());
                }
            }

            for (WebElement el : downloadElements) {
                if (el.isDisplayed()) {
                    visibleDownloadElements++;
                    System.out.println("Visible result/download element: " + el.getText());
                }
            }

            System.out.println("Visible convert buttons found: " + visibleConvertButtons);
            System.out.println("Visible result/download elements found: " + visibleDownloadElements);

            if (visibleConvertButtons > 0 || visibleDownloadElements > 0) {
                System.out.println("TC009 - Pass: Convert or result section appeared after file selection.");
            } else {
                System.out.println("TC009 - Fail: No convert or result section appeared after file selection.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC009_ResultSectionCheck");

        } catch (Exception e) {
            System.out.println("TC009 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
