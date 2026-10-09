package com.sqa;

public class TC001_HomePageLoad extends BaseSetup {
    public static void main(String[] args) {
        TC001_HomePageLoad test = new TC001_HomePageLoad();

        try {
            test.setUp();
            test.openSite();

            String title = test.driver.getTitle();

            if (title != null && !title.trim().isEmpty()) {
                System.out.println("TC001 - Pass: Homepage loaded successfully.");
            } else {
                System.out.println("TC001 - Fail: Homepage title is blank.");
            }

            ScreenshotUtil.captureScreenshot(test.driver, "TC001_HomePageLoad");

        } catch (Exception e) {
            System.out.println("TC001 - Fail: " + e.getMessage());
        } finally {
            test.tearDown();
        }
    }
}
