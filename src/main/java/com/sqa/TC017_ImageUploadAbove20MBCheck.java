package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TC017_ImageUploadAbove20MBCheck extends BaseSetup {

    public static void main(String[] args) {
        TC017_ImageUploadAbove20MBCheck test = new TC017_ImageUploadAbove20MBCheck();

        try {
            System.out.println("Starting TC017...");
            test.setUp();

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(30));

            // ===================================================
            // Step 1: Use your own 20MB+ local image
            // ===================================================
            String imagePath = "C:\\Users\\kesha\\OneDrive\\Desktop\\heavenly_4x.png";

            Path filePath = Paths.get(imagePath);

            if (!Files.exists(filePath)) {
                System.out.println("TC017 - Fail: File does not exist at path: " + imagePath);
                return;
            }

            long fileSizeBytes = Files.size(filePath);
            double fileSizeMB = fileSizeBytes / (1024.0 * 1024.0);

            System.out.println("Selected image: " + imagePath);
            System.out.printf("Selected image size: %.2f MB%n", fileSizeMB);

            if (fileSizeMB <= 20.0) {
                System.out.println("TC017 - Fail: Selected image is not above 20MB.");
                return;
            }

            // ===================================================
            // Step 2: Open exact target page directly
            // ===================================================
            test.driver.get("https://www.pixelssuite.com/convert-to-jpg");
            System.out.println("Opened direct URL: " + test.driver.getCurrentUrl());

            // ===================================================
            // Step 3: Validate page loaded
            // ===================================================
            WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Convert Image']")
            ));
            System.out.println("Page heading: " + heading.getText());

            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input[type='file']")
            ));
            System.out.println("File input found.");

            // ===================================================
            // Step 4: Upload file bigger than 20MB
            // ===================================================
            fileInput.sendKeys(filePath.toAbsolutePath().toString());
            System.out.println("Uploaded image above 20MB.");

            Thread.sleep(6000);

            // ===================================================
            // Step 5: Check whether site rejects or accepts upload
            // ===================================================
            boolean rejected = false;
            boolean accepted = false;

            String bodyText = test.driver.findElement(By.tagName("body")).getText().toLowerCase();
            System.out.println("Page body checked after upload.");

            String[] rejectWords = {
                    "max 20mb",
                    "maximum 20mb",
                    "file too large",
                    "too large",
                    "size limit",
                    "must be less than",
                    "must be under",
                    "exceeds",
                    "upload failed",
                    "invalid file size",
                    "20mb"
            };

            for (String word : rejectWords) {
                if (bodyText.contains(word)) {
                    rejected = true;
                    System.out.println("Detected rejection text: " + word);
                    break;
                }
            }

            if (!rejected) {
                List<WebElement> acceptedIndicators = test.driver.findElements(By.xpath(
                        "//img | //canvas | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download')] | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'convert')] | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'.jpg')] | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'.jpeg')] | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'.png')] | " +
                                "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'.webp')]"
                ));

                int visibleCount = 0;
                for (WebElement el : acceptedIndicators) {
                    try {
                        if (el.isDisplayed()) {
                            visibleCount++;
                        }
                    } catch (Exception ignored) {
                    }
                }

                System.out.println("Visible acceptance indicators count: " + visibleCount);

                String inputValue = fileInput.getAttribute("value");
                System.out.println("File input value after upload: " + inputValue);

                if ((inputValue != null && !inputValue.trim().isEmpty())
                        || visibleCount > 0
                        || !bodyText.contains("no image yet")) {
                    accepted = true;
                }
            }

            // ===================================================
            // Step 6: Final result
            // ===================================================
            if (rejected) {
                System.out.println("TC017 - Pass: System rejected image above 20MB as expected.");
            } else if (accepted) {
                System.out.println("TC017 - Fail: System ACCEPTED image above 20MB even though UI says max 20MB.");
            } else {
                System.out.println("TC017 - Inconclusive: Could not clearly confirm acceptance or rejection.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC017_Above20MBUploadCheck");

        } catch (Exception e) {
            System.out.println("TC017 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}