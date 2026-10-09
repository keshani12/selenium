package com.sqa;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TC002_NavigationMenuCheck extends BaseSetup {
    public static void main(String[] args) {
        TC002_NavigationMenuCheck test = new TC002_NavigationMenuCheck();

        try {
            System.out.println("Starting TC002...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            List<WebElement> links = test.driver.findElements(By.tagName("a"));
            int visibleLinks = 0;

            for (WebElement link : links) {
                if (link.isDisplayed()) {
                    visibleLinks++;
                }
            }

            System.out.println("Total links found: " + links.size());
            System.out.println("Visible links found: " + visibleLinks);

            if (visibleLinks > 0) {
                System.out.println("TC002 - Pass: Navigation/menu links are visible on the page.");
            } else {
                System.out.println("TC002 - Fail: No visible links found on the page.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC002_NavigationMenuCheck");

        } catch (Exception e) {
            System.out.println("TC002 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}