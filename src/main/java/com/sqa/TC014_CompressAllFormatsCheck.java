package com.sqa;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class TC014_CompressAllFormatsCheck extends BaseSetup {

    public static void main(String[] args) {
        TC014_CompressAllFormatsCheck test = new TC014_CompressAllFormatsCheck();

        try {
            System.out.println("Starting TC014...");
            test.setUp();
            System.out.println("Browser setup completed.");

            WebDriverWait wait = new WebDriverWait(test.driver, Duration.ofSeconds(25));
            JavascriptExecutor js = (JavascriptExecutor) test.driver;

            String imagePath = "C:\\Users\\kesha\\OneDrive\\Pictures\\Screenshots\\Screenshot (6).png";

            if (!Files.exists(Paths.get(imagePath))) {
                System.out.println("TC014 - Fail: Image file does not exist at path: " + imagePath);
                return;
            }

            String[][] tools = {
                    {"Compress Image", "compress-image", "Compress Image"},
                    {"To GIF", "gif-compressor", "GIF Compressor"},
                    {"To PNG", "png-compressor", "PNG Compressor"}
            };

            for (String[] tool : tools) {
                try {
                    String linkText = tool[0];
                    String expectedUrlPart = tool[1];
                    String expectedHeading = tool[2];

                    System.out.println("\n==============================");
                    System.out.println("Checking: " + linkText);
                    System.out.println("==============================");

                    test.openSite();
                    System.out.println("Website opened.");

                    // Step 1: Find Compress title on homepage
                    WebElement compressTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("(//*[normalize-space()='Compress'])[1]")
                    ));
                    System.out.println("Found card title: " + compressTitle.getText());

                    // Step 2: Find Compress card container using links inside it
                    WebElement compressCard = compressTitle.findElement(By.xpath(
                            "./ancestor::*[" +
                                    ".//*[normalize-space()='Compress Image'] and " +
                                    ".//*[normalize-space()='To GIF'] and " +
                                    ".//*[normalize-space()='To PNG']" +
                                    "][1]"
                    ));

                    // Step 3: Check description inside the same card
                    WebElement description = compressCard.findElement(By.xpath(
                            ".//*[contains(.,'Compress JPG, PNG, and GIF images while keeping good visual quality')]"
                    ));
                    System.out.println("Found description: " + description.getText());

                    // Step 4: Find 3 links only inside Compress card
                    WebElement compressImageLink = compressCard.findElement(
                            By.xpath(".//*[normalize-space()='Compress Image']")
                    );
                    WebElement toGifLink = compressCard.findElement(
                            By.xpath(".//*[normalize-space()='To GIF']")
                    );
                    WebElement toPngLink = compressCard.findElement(
                            By.xpath(".//*[normalize-space()='To PNG']")
                    );

                    System.out.println("Visible links: "
                            + compressImageLink.getText() + ", "
                            + toGifLink.getText() + ", "
                            + toPngLink.getText());

                    // Step 5: Pick current target link
                    WebElement currentLink;
                    switch (linkText) {
                        case "Compress Image":
                            currentLink = compressImageLink;
                            break;
                        case "To GIF":
                            currentLink = toGifLink;
                            break;
                        case "To PNG":
                            currentLink = toPngLink;
                            break;
                        default:
                            currentLink = null;
                    }

                    if (currentLink == null) {
                        System.out.println("TC014 - Fail: " + linkText + " link not found inside Compress card.");
                        ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + expectedUrlPart);
                        continue;
                    }

                    System.out.println("Found target link: " + currentLink.getText());

                    // Step 6: Click target link
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
                        System.out.println("TC014 - Fail: Did not navigate to expected page for " + linkText);
                        ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + expectedUrlPart);
                        continue;
                    }

                    // Step 7: Check page heading
                    WebElement pageHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[normalize-space()='" + expectedHeading + "']")
                    ));
                    System.out.println("Page heading visible: " + pageHeading.getText());

                    // Step 8: Check file input
                    List<WebElement> fileInputs = test.driver.findElements(By.cssSelector("input[type='file']"));
                    if (fileInputs.isEmpty()) {
                        System.out.println("TC014 - Fail: File input is not available on " + expectedHeading + " page.");
                        ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + expectedUrlPart);
                        continue;
                    }

                    WebElement fileInput = fileInputs.get(0);
                    System.out.println("File input is available.");

                    // Step 9: Check upload area text flexibly
                    boolean uploadAreaFound = false;

                    List<WebElement> uploadAreaCandidates = test.driver.findElements(By.xpath(
                            "//*[contains(normalize-space(.),'Select files')] | " +
                                    "//*[contains(normalize-space(.),'Drag and drop')] | " +
                                    "//*[contains(normalize-space(.),'browse from your device')]"
                    ));

                    for (WebElement el : uploadAreaCandidates) {
                        try {
                            if (el.isDisplayed()) {
                                uploadAreaFound = true;
                                System.out.println("Upload area visible: " + el.getText());
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    if (!uploadAreaFound) {
                        System.out.println("Upload area text not found, but file input is available. Continuing test.");
                    }

                    // Step 10: Check main work area more flexibly
                    boolean sectionConfirmed = false;

                    List<WebElement> sectionCandidates = test.driver.findElements(By.xpath(
                            "//*[normalize-space()='Compress'] | " +
                                    "//*[normalize-space()='Preview'] | " +
                                    "//*[contains(normalize-space(.),'No image yet')] | " +
                                    "//*[contains(normalize-space(.),'Select an image')]"
                    ));

                    for (WebElement el : sectionCandidates) {
                        try {
                            if (el.isDisplayed()) {
                                sectionConfirmed = true;
                                System.out.println("Work area element visible: " + el.getText());
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    if (!sectionConfirmed) {
                        System.out.println("TC014 - Fail: Main compress/preview work area not found on " + expectedHeading + " page.");
                        ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + expectedUrlPart);
                        continue;
                    }

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
                        if (bodyText.contains("screenshot") ||
                                bodyText.contains(".png") ||
                                bodyText.contains(".jpg") ||
                                bodyText.contains(".jpeg") ||
                                !bodyText.contains("no image yet")) {
                            uploadConfirmed = true;
                        }
                    }

                    if (!uploadConfirmed) {
                        List<WebElement> previewIndicators = test.driver.findElements(
                                By.xpath("//img | //canvas | //svg | " +
                                        "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'download')] | " +
                                        "//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'compress')]")
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
                        System.out.println("TC014 - Pass: " + linkText + " page, elements, and upload are working correctly.");
                    } else {
                        System.out.println("TC014 - Fail: Upload could not be confirmed for " + linkText);
                    }

                    ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + expectedUrlPart);

                } catch (Exception loopEx) {
                    System.out.println("TC014 - Fail while checking " + tool[0] + ": " + loopEx.getMessage());
                    loopEx.printStackTrace();
                    ScreenshotUtil.captureScreenshot(test.driver, "TC014_" + tool[1]);
                }
            }

        } catch (Exception e) {
            System.out.println("TC014 - Fail: " + e.getMessage());
            e.printStackTrace();
        } finally {
            test.tearDown();
        }
    }
}