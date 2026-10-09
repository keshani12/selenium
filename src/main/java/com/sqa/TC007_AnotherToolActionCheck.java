package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class TC007_AnotherToolActionCheck extends BaseSetup {
    public static void main(String[] args) {
        TC007_AnotherToolActionCheck test = new TC007_AnotherToolActionCheck();

        try {
            System.out.println("Starting TC007...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            JavascriptExecutor js = (JavascriptExecutor) test.driver;
            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(10));

            boolean clickedCrop = false;

            // Step 1: Open Crop menu/button
            List<WebElement> buttons = test.driver.findElements(By.tagName("button"));
            for (WebElement button : buttons) {
                String text = button.getText().trim().toLowerCase();
                if (text.equals("crop") || text.contains("crop")) {
                    System.out.println("Found Crop button: " + button.getText());
                    js.executeScript("arguments[0].click();", button);
                    clickedCrop = true;
                    break;
                }
            }

            if (!clickedCrop) {
                List<WebElement> links = test.driver.findElements(By.tagName("a"));
                for (WebElement link : links) {
                    String text = link.getText().trim().toLowerCase();
                    if (text.equals("crop") || text.contains("crop")) {
                        System.out.println("Found Crop link: " + link.getText());
                        js.executeScript("arguments[0].click();", link);
                        clickedCrop = true;
                        break;
                    }
                }
            }

            if (!clickedCrop) {
                System.out.println("TC007 - Fail: Crop feature not found.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC007_AnotherToolActionCheck");
                return;
            }

            Thread.sleep(3000);

            String currentUrl = test.driver.getCurrentUrl().toLowerCase();
            String pageSource = test.driver.getPageSource().toLowerCase();

            System.out.println("Current URL: " + currentUrl);

            // Step 2: Check for crop-related controls or upload area
            List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));
            List<WebElement> cropControls = test.driver.findElements(
                    By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'crop') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upload') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'drop file') " +
                            "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'image')]")
            );

            int visibleControls = 0;

            for (WebElement el : cropControls) {
                try {
                    if (el.isDisplayed()) {
                        visibleControls++;
                        System.out.println("Visible crop-related element: " + el.getText());
                    }
                } catch (Exception ignored) {
                }
            }

            System.out.println("File inputs found: " + fileInputs.size());
            System.out.println("Visible crop-related elements found: " + visibleControls);

            boolean toolReady =
                    currentUrl.contains("crop")
                            || !fileInputs.isEmpty()
                            || visibleControls > 0
                            || pageSource.contains("crop");

            if (toolReady) {
                System.out.println("TC007 - Pass: Crop tool action area is available.");
            } else {
                System.out.println("TC007 - Fail: Crop tool action area was not detected.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC007_AnotherToolActionCheck");

        } catch (Exception e) {
            System.out.println("TC007 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
