package com.apitesting.demo.guides;

import com.apitesting.demo.tests.BaseTest;
import com.apitesting.demo.utils.TestLogger;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Simple Actions Guide")
public class SimpleActionsGuideTest extends BaseTest {

    private Playwright playwrightBrowser;
    private Browser browser;
    private BrowserContext browserContext;
    private Page page;
    private String currentTestName;

    @BeforeEach
    void openBrowser(TestInfo testInfo) {
        currentTestName = testInfo.getDisplayName();
        playwrightBrowser = Playwright.create();
        browser = playwrightBrowser.chromium().launch();
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeBrowser() {
        if (page != null) {
            page.close();
        }
        if (browserContext != null) {
            browserContext.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwrightBrowser != null) {
            playwrightBrowser.close();
        }
    }

    @Test
    @DisplayName("G01 - Demonstrate simple form actions")
    void demonstrateSimpleActions() {
        page.navigate("https://automationexercise.com/login");
        page.waitForLoadState();

        String uniqueEmail = "copilot.demo." + System.currentTimeMillis() + "@example.com";

        fillSignupStartForm("Guide User", uniqueEmail);
        openAccountDetailsForm();
        demonstrateFieldActions();

        TestLogger.log(currentTestName, "Simple actions guide completed successfully");
    }

    private void fillSignupStartForm(String name, String email) {
        page.locator("input[data-qa='signup-name']").fill(name);
        page.locator("input[data-qa='signup-email']").fill(email);
        assertEquals(name, page.locator("input[data-qa='signup-name']").inputValue());
        assertEquals(email, page.locator("input[data-qa='signup-email']").inputValue());

        page.locator("button[data-qa='signup-button']").click();
        page.waitForLoadState();
    }

    private void openAccountDetailsForm() {
        page.locator("#id_gender1").click();
        page.locator("#password").fill("Demo@1234");
        assertTrue(page.locator("#id_gender1").isChecked(), "Radio button should be selected");
    }

    private void demonstrateFieldActions() {
        page.locator("#days").selectOption("10");
        page.locator("#months").selectOption("May");
        page.locator("#years").selectOption("1995");

        page.locator("#newsletter").check();
        page.locator("#optin").check();

        page.locator("#first_name").fill("Guide");
        page.locator("#last_name").fill("User");
        page.locator("#address1").type("123 Demo Street");

        page.locator("#state").fill("Ontario");
        page.locator("#city").fill("Toronto");
        page.locator("#zipcode").fill("M5V2T6");
        page.locator("#mobile_number").fill("1234567890");

        page.locator("#country").selectOption("Canada");

        assertEquals("10", page.locator("#days").inputValue());
        assertEquals("5", page.locator("#months").inputValue());
        assertEquals("1995", page.locator("#years").inputValue());
        assertTrue(page.locator("#newsletter").isChecked(), "Newsletter checkbox should be selected");
        assertTrue(page.locator("#optin").isChecked(), "Special offers checkbox should be selected");
    }
}
