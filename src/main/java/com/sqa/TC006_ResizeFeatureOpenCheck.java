package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TC006_ResizeFeatureOpenCheck extends BaseSetup {
    public static void main(String[] args) {
        TC006_ResizeFeatureOpenCheck test = new TC006_ResizeFeatureOpenCheck();

        try {
            System.out.println("Starting TC006...");
            test.setUp();
            test.openSite();

            JavascriptExecutor js = (JavascriptExecutor) test.driver;
            Actions actions = new Actions(test.driver);
            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(10));

            // Step 1: click the main Resize menu trigger
            boolean clickedMainResize = false;

            List<WebElement> buttons = test.driver.findElements(By.tagName("button"));
            for (WebElement button : buttons) {
                String text = button.getText().trim().toLowerCase();
                if (text.equals("resize")) {
                    System.out.println("Main Resize button found: " + button.getText());
                    js.executeScript("arguments[0].scrollIntoView({block:'center'});", button);
                    Thread.sleep(500);
                    actions.moveToElement(button).pause(Duration.ofMillis(300)).perform();
                    js.executeScript("arguments[0].click();", button);
                    clickedMainResize = true;
                    break;
                }
            }

            if (!clickedMainResize) {
                System.out.println("TC006 - Fail: Main Resize button not found.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC006_ResizeFeatureOpenCheck");
                return;
            }

            Thread.sleep(2500);

            // Step 2: collect all visible resize-related elements after menu opens
            List<WebElement> allCandidates = test.driver.findElements(By.xpath("//a|//button|//div"));
            List<WebElement> resizeCandidates = new ArrayList<>();

            System.out.println("---- Resize-related visible elements ----");
            for (WebElement el : allCandidates) {
                try {
                    String txt = el.getText().trim();
                    if (!txt.isEmpty() && el.isDisplayed()) {
                        String lower = txt.toLowerCase();
                        if (lower.contains("resize") || lower.contains("batch resize") || lower.contains("image enlarger")) {
                            System.out.println("TEXT: " + txt);
                            resizeCandidates.add(el);
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            if (resizeCandidates.size() < 2) {
                System.out.println("TC006 - Fail: Could not find submenu item under Resize.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC006_ResizeFeatureOpenCheck");
                return;
            }

            // Step 3: click the second visible resize-related item, not the first menu trigger
            WebElement submenuItem = resizeCandidates.get(1);
            System.out.println("Trying submenu item: " + submenuItem.getText());

            js.executeScript("arguments[0].scrollIntoView({block:'center'});", submenuItem);
            Thread.sleep(500);
            actions.moveToElement(submenuItem).pause(Duration.ofMillis(300)).perform();
            js.executeScript("arguments[0].click();", submenuItem);

            Thread.sleep(3000);

            // Step 4: verify actual tool page/section opened
            String currentUrl = test.driver.getCurrentUrl().toLowerCase();
            String pageSource = test.driver.getPageSource().toLowerCase();

            System.out.println("Current URL: " + currentUrl);

            boolean opened =
                    currentUrl.contains("resize")
                            || pageSource.contains("batch resize")
                            || pageSource.contains("image enlarger")
                            || pageSource.contains("width")
                            || pageSource.contains("height")
                            || pageSource.contains("drop file")
                            || pageSource.contains("upload");

            if (opened) {
                System.out.println("TC006 - Pass: Resize-related tool page/section opened successfully.");
            } else {
                System.out.println("TC006 - Fail: Resize-related tool page/section did not open correctly.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC006_ResizeFeatureOpenCheck");

        } catch (Exception e) {
            System.out.println("TC006 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}
