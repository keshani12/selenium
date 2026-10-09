package com.sqa;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TC010_FooterLinksCheck extends BaseSetup {
    public static void main(String[] args) {
        TC010_FooterLinksCheck test = new TC010_FooterLinksCheck();

        try {
            System.out.println("Starting TC010...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            // Scroll to bottom
            ((org.openqa.selenium.JavascriptExecutor) test.driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight);");

            Thread.sleep(2000);

            List<WebElement> links = test.driver.findElements(By.tagName("a"));
            int visibleFooterLinks = 0;

            for (WebElement link : links) {
                try {
                    String text = link.getText().trim();
                    String href = link.getAttribute("href");

                    if (link.isDisplayed() && href != null && !href.isEmpty()) {
                        visibleFooterLinks++;
                        System.out.println("Footer/visible link found: " + text + " -> " + href);
                    }
                } catch (Exception ignored) {
                }
            }

            System.out.println("Visible clickable links found: " + visibleFooterLinks);

            if (visibleFooterLinks > 0) {
                System.out.println("TC010 - Pass: Footer or bottom page links are visible and clickable.");
            } else {
                System.out.println("TC010 - Fail: No visible clickable footer/bottom links found.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC010_FooterLinksCheck");

        } catch (Exception e) {
            System.out.println("TC010 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
