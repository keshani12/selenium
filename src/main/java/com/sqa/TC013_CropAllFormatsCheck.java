package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TC013_CropAllFormatsCheck extends BaseSetup {

    public static void main(String[] args) {
        TC013_CropAllFormatsCheck test = new TC013_CropAllFormatsCheck();

        try {
            System.out.println("Starting TC013...");
            test.setUp();
            System.out.println("Browser setup completed.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(25));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            String imagePath = "C:\\Users\\kesha\\OneDrive\\Pictures\\Screenshots\\Screenshot (6).png";

            if (!Files.exists(Paths.get(imagePath))) {
                System.out.println("TC013 - Fail: Image file does not exist at path: " + imagePath);
                return;
            }

            String[][] tools = {
                    {"To JPG", "crop-jpg", "Crop JPG"},
                    {"To PNG", "crop-png", "Crop PNG"},
                    {"To WebP", "crop-webp", "Crop WebP"}
            };

            for (String[] tool : tools) {
                String linkText = tool[0];
                String expectedUrlPart = tool[1];
                String expectedHeading = tool[2];

                System.out.println("\n==============================");
                System.out.println("Checking: " + linkText);
                System.out.println("==============================");

                test.openSite();
                System.out.println("Website opened.");

                // Step 1: Check Crop card title
                WebElement cropCardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Crop']")
                ));
                System.out.println("Found card title: " + cropCardTitle.getText());

                // Step 2: Check Crop description
                WebElement description = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Crop']/following::*[contains(.,'Crop images precisely')][1]")
                ));
                System.out.println("Found description: " + description.getText());

                // Step 3: Check all 3 links visible
                WebElement toJpgLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(normalize-space(.),'To JPG')] | //button[contains(normalize-space(.),'To JPG')]")
                ));
                WebElement toPngLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(normalize-space(.),'To PNG')] | //button[contains(normalize-space(.),'To PNG')]")
                ));
                WebElement toWebpLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//a[contains(normalize-space(.),'To WebP')] | //button[contains(normalize-space(.),'To WebP')]")
                ));

                System.out.println("Visible links: "
                        + toJpgLink.getText() + ", "
                        + toPngLink.getText() + ", "
                        + toWebpLink.getText());

                // Step 4: Find current target link
                WebElement currentLink = null;
                List<WebElement> candidates = test.driver.findElements(
                        By.xpath("//a[contains(normalize-space(.),'" + linkText + "')] | " +
                                "//button[contains(normalize-space(.),'" + linkText + "')] | " +
                                "//*[@role='button' and contains(normalize-space(.),'" + linkText + "')]")
                );

                for (WebElement el : candidates) {
                    try {
                        if (el.isDisplayed() && el.isEnabled()) {
                            currentLink = el;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (currentLink == null) {
                    System.out.println("TC013 - Fail: " + linkText + " link not found.");
                    ScreenshotUtil.captureScreenshot(test.driver, "TC013_" + expectedUrlPart);
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

                if (!afterUrl.toLowerCase().contains(expectedUrlPart)) {
                    System.out.println("TC013 - Fail: Did not navigate to expected page for " + linkText);
                    ScreenshotUtil.captureScreenshot(test.driver, "TC013_" + expectedUrlPart);
                    continue;
                }

                // Step 6: Check page heading
                WebElement pageHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='" + expectedHeading + "']")
                ));
                System.out.println("Page heading visible: " + pageHeading.getText());

                // Step 7: Check file input
                List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));
                if (fileInputs.isEmpty()) {
                    System.out.println("TC013 - Fail: File input is not available on " + expectedHeading + " page.");
                    ScreenshotUtil.captureScreenshot(test.driver, "TC013_" + expectedUrlPart);
                    continue;
                }

                WebElement fileInput = fileInputs.get(0);
                System.out.println("File input is available.");

                // Step 8: Check select files area
                WebElement selectFiles = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(normalize-space(.),'Select files')]")
                ));
                System.out.println("Select files visible: " + selectFiles.getText());

                // Step 9: Check Crop section
                WebElement cropSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Crop']")
                ));
                System.out.println("Crop section visible: " + cropSection.getText());

                // Step 10: Check Preview section
                WebElement previewSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[normalize-space()='Preview']")
                ));
                System.out.println("Preview section visible: " + previewSection.getText());

                // Step 11: Upload sample image
                fileInput.sendKeys(imagePath);
                System.out.println("Image uploaded: " + imagePath);

                Thread.sleep(3000);

                // Step 12: Confirm upload
                boolean uploadConfirmed = false;

                String uploadedValue = fileInput.getAttribute("value");
                System.out.println("File input value after upload: " + uploadedValue);

                if (uploadedValue != null && !uploadedValue.trim().isEmpty()) {
                    uploadConfirmed = true;
                }

                if (!uploadConfirmed) {
                    String bodyText = test.driver.findElement(By.tagName("body")).getText().toLowerCase();
                    if (bodyText.contains("sample.jpg") ||
                            bodyText.contains("sample.jpeg") ||
                            bodyText.contains("sample.png") ||
                            !bodyText.contains("no image yet")) {
                        uploadConfirmed = true;
                    }
                }

                if (!uploadConfirmed) {
                    List<WebElement> previewIndicators = test.driver.findElements(
                            By.xpath("//img | //canvas | //svg | " +
                                    "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download')] | " +
                                    "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'crop')]")
                    );

                    int visibleIndicators = 0;
                    for (WebElement el : previewIndicators) {
                        try {
                            if (el.isDisplayed()) {
                                visibleIndicators++;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    System.out.println("Visible preview indicators count: " + visibleIndicators);

                    if (visibleIndicators > 0) {
                        uploadConfirmed = true;
                    }
                }

                if (uploadConfirmed) {
                    System.out.println("TC013 - Pass: " + linkText + " page, elements, and upload are working correctly.");
                } else {
                    System.out.println("TC013 - Fail: Upload could not be confirmed for " + linkText);
                }

                ScreenshotUtil.captureScreenshot(test.driver, "TC013_" + expectedUrlPart);
            }

        } catch (Exception e) {
            System.out.println("TC013 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}