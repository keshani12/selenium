package com.sqa;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class BaseSetup {
    protected WebDriver driver;
    protected String baseUrl = "https://www.pixelssuite.com/";

    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    public void openSite() {
        driver.get(baseUrl);
    }

    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
