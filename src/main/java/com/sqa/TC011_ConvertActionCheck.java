package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TC011_ConvertActionCheck extends BaseSetup {

    public static void main(String[] args) {
        TC011_ConvertActionCheck test = new TC011_ConvertActionCheck();

        try {
            System.out.println("Starting TC011...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(30));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            // Step 1: Open Document Converter
            WebElement mainMenu = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'document converter')]")
            ));
            js.executeScript("arguments[0].click();", mainMenu);
            System.out.println("Found main button: " + mainMenu.getText());

            Thread.sleep(1500);

            // Step 2: Open PDF -> Word
            WebElement pdfToWordButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(.,'PDF') and contains(.,'Word')]")
            ));
            js.executeScript("arguments[0].click();", pdfToWordButton);
            System.out.println("Found sub-tool: " + pdfToWordButton.getText());

            Thread.sleep(2500);

            // Step 3: Upload file
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='file']")
            ));

            String filePath = "C:\\Users\\kesha\\OneDrive\\Desktop\\sample.pdf";

            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("TC011 - Fail: File does not exist at path: " + filePath);
                ScreenshotUtil.captureScreenshot(test.driver, "TC011_ConvertActionCheck");
                return;
            }

            fileInput.sendKeys(filePath);
            System.out.println("File path sent successfully: " + filePath);

            // Wait until selected file name appears
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(),'sample.pdf')]")),
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'selected')]"))
            ));

            Thread.sleep(2000);

            logVisibleCandidates(test.driver);

            // Step 4: Find the exact Convert to Word button
            WebElement convertButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[normalize-space(.)='Convert to Word']")
            ));

            js.executeScript("arguments[0].scrollIntoView({block:'center'});", convertButton);
            Thread.sleep(1000);

            String beforeText = safe(getBodyText(test.driver));
            String beforeUrl = test.driver.getCurrentUrl();

            boolean clicked = clickReliably(test.driver, convertButton, js);

            if (!clicked) {
                System.out.println("TC011 - Fail: Could not trigger Convert to Word button.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC011_ConvertActionCheck");
                return;
            }

            System.out.println("Clicked Convert to Word button.");

            // Step 5: Wait for actual state change
            boolean changed = waitForStateChange(test.driver, wait, beforeUrl, beforeText);

            logBodyPreview(test.driver);
            logVisibleCandidates(test.driver);
            ScreenshotUtil.captureScreenshot(test.driver, "TC011_ConvertActionCheck");

            if (changed) {
                System.out.println("TC011 - Pass: Conversion action triggered and page state changed.");
            } else {
                System.out.println("TC011 - Fail: Convert button was clicked, but no conversion state change happened.");
            }

        } catch (Exception e) {
            System.out.println("TC011 - Fail: " + e.getMessage());
            e.printStackTrace();
            ScreenshotUtil.captureScreenshot(test.driver, "TC011_ConvertActionCheck");
        } finally {
            test.tearDown();
        }
    }

    private static boolean clickReliably(WebDriver driver, WebElement element, JavascriptExecutor js) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            return true;
        } catch (Exception ignored) {
        }

        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(element).pause(Duration.ofMillis(300)).click().perform();
            return true;
        } catch (Exception ignored) {
        }

        try {
            js.executeScript("arguments[0].click();", element);
            return true;
        } catch (Exception ignored) {
        }

        try {
            js.executeScript(
                    "arguments[0].dispatchEvent(new MouseEvent('click', {bubbles:true,cancelable:true,view:window}));",
                    element
            );
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    private static boolean waitForStateChange(WebDriver driver, WebDriverWait wait, String beforeUrl, String beforeText) {
        try {
            return wait.until(d -> {
                try {
                    String currentUrl = d.getCurrentUrl();
                    String currentText = safe(getBodyText(d));

                    if (!currentUrl.equals(beforeUrl)) {
                        System.out.println("Detected URL change.");
                        return true;
                    }

                    if (!currentText.equals(beforeText)) {
                        System.out.println("Detected page text change.");
                        return true;
                    }

                    List<WebElement> results = d.findElements(By.xpath(
                            "//*[contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download')" +
                                    " or contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'ready')" +
                                    " or contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'completed')" +
                                    " or contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'success')" +
                                    " or contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'converted')" +
                                    " or contains(translate(normalize-space(text()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'docx')]"
                    ));

                    for (WebElement el : results) {
                        try {
                            if (el.isDisplayed()) {
                                System.out.println("Detected result element: " + el.getText());
                                return true;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                } catch (Exception ignored) {
                }
                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }

    private static String getBodyText(WebDriver driver) {
        try {
            return driver.findElement(By.tagName("body")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private static void logVisibleCandidates(WebDriver driver) {
        try {
            List<WebElement> all = driver.findElements(By.xpath(
                    "//button | //a | //*[@role='button'] | //input[@type='button'] | //input[@type='submit']"
            ));

            System.out.println("---- Visible clickable candidates ----");
            for (WebElement el : all) {
                try {
                    if (el.isDisplayed()) {
                        String text = safe(el.getText()).trim();
                        String value = safe(el.getAttribute("value")).trim();
                        String aria = safe(el.getAttribute("aria-label")).trim();
                        String title = safe(el.getAttribute("title")).trim();

                        String combined = (text + " | " + value + " | " + aria + " | " + title).trim();
                        if (!combined.replace("|", "").trim().isEmpty()) {
                            System.out.println(combined);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            System.out.println("------------------------------------");
        } catch (Exception ignored) {
        }
    }

    private static void logBodyPreview(WebDriver driver) {
        try {
            String bodyText = safe(driver.findElement(By.tagName("body")).getText());
            System.out.println("---- PAGE TEXT AFTER CLICK ----");
            if (bodyText.length() > 1500) {
                System.out.println(bodyText.substring(0, 1500));
            } else {
                System.out.println(bodyText);
            }
            System.out.println("--------------------------------");
        } catch (Exception ignored) {
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}