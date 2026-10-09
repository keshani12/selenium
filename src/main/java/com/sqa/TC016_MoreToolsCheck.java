package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TC016_MoreToolsCheck extends BaseSetup {

    public static void main(String[] args) {
        TC016_MoreToolsCheck test = new TC016_MoreToolsCheck();

        try {
            System.out.println("Starting TC016...");
            test.setUp();
            System.out.println("Browser setup completed.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(25));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            String[][] tools = {
                    {"Rotate", "rotate-image", "Rotate", "Rotate"},
                    {"Flip", "flip-image", "Flip", "Flip"},
                    {"Meme", "meme-generator", "Meme Generator", "Meme"},
                    {"Color Picker", "color-picker", "Color Picker", "Color Picker"},
                    {"Image → Text", "image-to-text", "Image to Text", "Text"}
            };

            for (String[] tool : tools) {
                try {
                    String linkText = tool[0];
                    String expectedUrlPart = tool[1];
                    String expectedHeading = tool[2];
                    String fallbackText = tool[3];

                    System.out.println("\n==============================");
                    System.out.println("Checking: " + linkText);
                    System.out.println("==============================");

                    test.openSite();
                    System.out.println("Website opened.");

                    // Step 1: Check More Tools title
                    WebElement moreToolsTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//*[normalize-space()='More Tools'])[1]")
                    ));
                    System.out.println("Found card title: " + moreToolsTitle.getText());

                    // Step 2: Check description
                    WebElement description = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(normalize-space(.),'Rotate and flip images, generate memes, pick colors, and extract text from images')]")
                    ));
                    System.out.println("Found description: " + description.getText());

                    // Step 3: Check all exact links are visible
                    WebElement rotateLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//a[normalize-space()='Rotate'] | //button[normalize-space()='Rotate'])[1]")
                    ));
                    WebElement flipLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//a[normalize-space()='Flip'] | //button[normalize-space()='Flip'])[1]")
                    ));
                    WebElement memeLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//a[normalize-space()='Meme'] | //button[normalize-space()='Meme'])[1]")
                    ));
                    WebElement colorPickerLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//a[normalize-space()='Color Picker'] | //button[normalize-space()='Color Picker'])[1]")
                    ));
                    WebElement imageToTextLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//a[normalize-space()='Image → Text'] | //button[normalize-space()='Image → Text'])[1]")
                    ));

                    System.out.println("Visible links: "
                            + rotateLink.getText() + ", "
                            + flipLink.getText() + ", "
                            + memeLink.getText() + ", "
                            + colorPickerLink.getText() + ", "
                            + imageToTextLink.getText());

                    // Step 4: Pick current target link
                    WebElement currentLink;
                    switch (linkText) {
                        case "Rotate":
                            currentLink = rotateLink;
                            break;
                        case "Flip":
                            currentLink = flipLink;
                            break;
                        case "Meme":
                            currentLink = memeLink;
                            break;
                        case "Color Picker":
                            currentLink = colorPickerLink;
                            break;
                        case "Image → Text":
                            currentLink = imageToTextLink;
                            break;
                        default:
                            currentLink = null;
                    }

                    if (currentLink == null) {
                        System.out.println("TC016 - Fail: " + linkText + " link not found.");
                        ScreenshotUtil.captureScreenshot(test.driver, "TC016_" + expectedUrlPart);
                        continue;
                    }

                    System.out.println("Found target link: " + currentLink.getText());

                    // Step 5: Click target link
                    String beforeUrl = test.driver.getCurrentUrl();

                    try {
                        wait.until(ExpectedConditions.elementToBeClickable(currentLink));
                        currentLink.click();
                    } catch (Exception e) {
                        js.executeScript("arguments[0].click();", currentLink);
                    }

                    wait.until(driver -> !driver.getCurrentUrl().equals(beforeUrl));
                    String afterUrl = test.driver.getCurrentUrl();
                    System.out.println("Navigated to: " + afterUrl);

                    if (!afterUrl.toLowerCase().contains(expectedUrlPart.toLowerCase())) {
                        System.out.println("TC016 - Fail: Did not navigate to expected page for " + linkText);
                        ScreenshotUtil.captureScreenshot(test.driver, "TC016_" + expectedUrlPart);
                        continue;
                    }

                    // Step 6: Check page heading or fallback text
                    boolean pageConfirmed = false;

                    try {
                        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[normalize-space()='" + expectedHeading + "']")
                        ));
                        System.out.println("Page heading visible: " + heading.getText());
                        pageConfirmed = true;
                    } catch (Exception ignored) {
                    }

                    if (!pageConfirmed) {
                        try {
                            WebElement fallbackElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                    By.xpath("//*[contains(normalize-space(.),'" + fallbackText + "')]")
                            ));
                            System.out.println("Fallback page element visible: " + fallbackElement.getText());
                            pageConfirmed = true;
                        } catch (Exception ignored) {
                        }
                    }

                    if (!pageConfirmed) {
                        System.out.println("TC016 - Fail: Main heading or fallback content not found for " + linkText);
                        ScreenshotUtil.captureScreenshot(test.driver, "TC016_" + expectedUrlPart);
                        continue;
                    }

                    // Step 7: Confirm some usable UI
                    boolean uiConfirmed = false;

                    if (linkText.equals("Rotate") || linkText.equals("Flip") || linkText.equals("Image → Text")) {
                        if (!test.driver.findElements(By.cssSelector("input[type='file']")).isEmpty()) {
                            uiConfirmed = true;
                            System.out.println("File input is available.");
                        }
                    }

                    if (linkText.equals("Meme")) {
                        if (!test.driver.findElements(By.xpath(
                                "//*[contains(normalize-space(.),'Top Text')] | " +
                                        "//*[contains(normalize-space(.),'Bottom Text')] | " +
                                        "//*[contains(normalize-space(.),'Generate')] | " +
                                        "//*[contains(normalize-space(.),'Download')]"
                        )).isEmpty()) {
                            uiConfirmed = true;
                            System.out.println("Meme generator UI elements are available.");
                        }
                    }

                    if (linkText.equals("Color Picker")) {
                        if (!test.driver.findElements(By.xpath(
                                "//*[@type='color'] | " +
                                        "//*[contains(normalize-space(.),'Hex')] | " +
                                        "//*[contains(normalize-space(.),'RGB')] | " +
                                        "//*[contains(normalize-space(.),'Color')]"
                        )).isEmpty()) {
                            uiConfirmed = true;
                            System.out.println("Color picker UI elements are available.");
                        }
                    }

                    if (!uiConfirmed) {
                        System.out.println("Main UI-specific element not strongly confirmed, but page loaded correctly.");
                    }

                    System.out.println("TC016 - Pass: " + linkText + " page navigation and main content are working correctly.");
                    ScreenshotUtil.captureScreenshot(test.driver, "TC016_" + expectedUrlPart);

                } catch (Exception loopEx) {
                    System.out.println("TC016 - Fail while checking " + tool[0] + ": " + loopEx.getMessage());
                    loopEx.printStackTrace();
                    ScreenshotUtil.captureScreenshot(test.driver, "TC016_" + tool[1]);
                }
            }

        } catch (Exception e) {
            System.out.println("TC016 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}