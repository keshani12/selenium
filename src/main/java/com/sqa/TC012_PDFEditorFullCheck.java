package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TC012_PDFEditorFullCheck extends BaseSetup {

    public static void main(String[] args) {
        TC012_PDFEditorFullCheck test = new TC012_PDFEditorFullCheck();

        try {
            System.out.println("Starting TC012...");
            test.setUp();
            System.out.println("Browser setup completed.");

            test.openSite();
            System.out.println("Website opened.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(25));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            // Step 1: Check PDF Editor card title
            WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='PDF Editor']")
            ));
            System.out.println("Found title: " + title.getText());

            // Step 2: Check description more precisely
            WebElement description = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='PDF Editor']/following::*[contains(.,'Edit PDF files directly in your browser')][1]")
            ));
            System.out.println("Found description: " + description.getText());

            // Step 3: Find Open Editor link/button
            WebElement openEditor = null;
            List<WebElement> candidates = test.driver.findElements(
                    By.xpath("//a[contains(normalize-space(.),'Open Editor')] | " +
                            "//button[contains(normalize-space(.),'Open Editor')] | " +
                            "//*[@role='button' and contains(normalize-space(.),'Open Editor')]")
            );

            for (WebElement el : candidates) {
                try {
                    if (el.isDisplayed() && el.isEnabled()) {
                        openEditor = el;
                        break;
                    }
                } catch (Exception ignored) {
                }
            }

            if (openEditor == null) {
                System.out.println("TC012 - Fail: Open Editor link not found.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");
                return;
            }

            System.out.println("Found Open Editor: " + openEditor.getText());

            // Step 4: Click Open Editor
            String beforeUrl = test.driver.getCurrentUrl();

            try {
                wait.until(ExpectedConditions.elementToBeClickable(openEditor));
                openEditor.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", openEditor);
            }

            wait.until(driver -> !driver.getCurrentUrl().equals(beforeUrl));
            String afterUrl = test.driver.getCurrentUrl();
            System.out.println("Navigated to: " + afterUrl);

            if (!afterUrl.toLowerCase().contains("pdf-editor")) {
                System.out.println("TC012 - Fail: Did not navigate to PDF Editor page.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");
                return;
            }

            // Step 5: Check PDF Editor page heading
            WebElement editorHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='PDF Editor']")
            ));
            System.out.println("Editor heading visible: " + editorHeading.getText());

            // Step 6: Check file input
            List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));
            if (fileInputs.isEmpty()) {
                System.out.println("TC012 - Fail: File input is not available on PDF Editor page.");
                ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");
                return;
            }

            WebElement fileInput = fileInputs.get(0);
            System.out.println("File input is available on PDF Editor page.");

            // Step 7: Check Download button
            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(normalize-space(.),'Download')] | //*[@role='button' and contains(normalize-space(.),'Download')]")
            ));
            System.out.println("Download button visible: " + downloadButton.getText());

            // Step 8: Check Toolbar text
            WebElement toolbarText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Toolbar']")
            ));
            System.out.println("Toolbar visible: " + toolbarText.getText());

            // Step 9: Check Page section
            WebElement pageSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[normalize-space()='Page']")
            ));
            System.out.println("Page section visible: " + pageSection.getText());

            // Step 10: Upload sample PDF
            String filePath = "C:\\Users\\kesha\\OneDrive\\Desktop\\sample.pdf";

            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("TC012 - Fail: File does not exist at path: " + filePath);
                ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");
                return;
            }

            fileInput.sendKeys(filePath);
            System.out.println("File uploaded: " + filePath);

            Thread.sleep(3000);

            // Step 11: Check uploaded file confirmation
            boolean uploadConfirmed = false;

            String uploadedValue = fileInput.getAttribute("value");
            System.out.println("File input value after upload: " + uploadedValue);

            if (uploadedValue != null && !uploadedValue.trim().isEmpty()) {
                uploadConfirmed = true;
            }

            if (!uploadConfirmed) {
                String bodyText = test.driver.findElement(By.tagName("body")).getText().toLowerCase();
                if (bodyText.contains("sample.pdf")) {
                    uploadConfirmed = true;
                }
            }

            if (!uploadConfirmed) {
                List<WebElement> previewIndicators = test.driver.findElements(
                        By.xpath("//*[contains(normalize-space(.),'Prev')] | " +
                                "//*[contains(normalize-space(.),'Next')] | " +
                                "//canvas | //img | //svg")
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
                System.out.println("TC012 - Pass: PDF Editor card, navigation, page elements, and file upload are working correctly.");
            } else {
                System.out.println("TC012 - Fail: File upload could not be confirmed.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");

        } catch (Exception e) {
            System.out.println("TC012 - Fail: " + e.getMessage());
            e.printStackTrace();
            ScreenshotUtil.captureScreenshot(test.driver, "TC012_PDFEditorFullCheck");
        } finally {
            test.tearDown();
        }
    }
}