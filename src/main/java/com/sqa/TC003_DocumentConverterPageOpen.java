package com.sqa;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TC003_DocumentConverterPageOpen extends BaseSetup {
    public static void main(String[] args) {
        TC003_DocumentConverterPageOpen test = new TC003_DocumentConverterPageOpen();

        try {
            System.out.println("Starting TC003...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            boolean clicked = false;

            // Check all links first
            List<WebElement> links = test.driver.findElements(By.tagName("a"));
            for (WebElement link : links) {
                String text = link.getText().trim().toLowerCase();
                String href = link.getAttribute("href");

                if (text.contains("document") || text.contains("converter") ||
                        (href != null && href.toLowerCase().contains("document"))) {
                    System.out.println("Found link: " + link.getText());
                    link.click();
                    clicked = true;
                    break;
                }
            }

            // If not found in links, check buttons
            if (!clicked) {
                List<WebElement> buttons = test.driver.findElements(By.tagName("button"));
                for (WebElement button : buttons) {
                    String text = button.getText().trim().toLowerCase();

                    if (text.contains("document") || text.contains("converter")) {
                        System.out.println("Found button: " + button.getText());
                        button.click();
                        clicked = true;
                        break;
                    }
                }
            }

            if (!clicked) {
                System.out.println("TC003 - Fail: Document Converter link/button not found.");
            } else {
                Thread.sleep(3000);

                String currentUrl = test.driver.getCurrentUrl().toLowerCase();
                String pageSource = test.driver.getPageSource().toLowerCase();

                System.out.println("Current URL: " + currentUrl);

                if (currentUrl.contains("document") ||
                        pageSource.contains("document converter") ||
                        pageSource.contains("pdf") ||
                        pageSource.contains("word")) {
                    System.out.println("TC003 - Pass: Document Converter page opened successfully.");
                } else {
                    System.out.println("TC003 - Fail: Document Converter page did not open correctly.");
                }
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC003_DocumentConverterPageOpen");

        } catch (Exception e) {
            System.out.println("TC003 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}