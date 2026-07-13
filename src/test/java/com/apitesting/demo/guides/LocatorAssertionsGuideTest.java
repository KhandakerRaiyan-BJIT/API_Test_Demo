package com.apitesting.demo.guides;

import com.apitesting.demo.tests.BaseTest;
import com.apitesting.demo.utils.APIConstants;
import com.apitesting.demo.utils.TestLogger;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Locator Assertions Guide")
public class LocatorAssertionsGuideTest extends BaseTest {

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
    @DisplayName("A01 - Demonstrate common locator assertions")
    void demonstrateLocatorAssertions() {
        page.navigate("https://automationexercise.com/");
        assertThat(page).hasTitle(Pattern.compile("Automation Exercise"));
        assertThat(page).hasURL(Pattern.compile("automationexercise\\.com"));

        loadAssertionsDemoPage();
        runLocatorAssertions();
        runResponseAssertion();

        TestLogger.log(currentTestName, "Locator assertions guide completed successfully");
    }

    @Test
    @DisplayName("A02 - Reuse signed in state for authenticated assertions")
    void reuseSignedInState() throws Exception {
        String email = System.getenv("AE_LOGIN_EMAIL");
        String password = System.getenv("AE_LOGIN_PASSWORD");

        assumeTrue(isNotBlank(email) && isNotBlank(password),
                "Set AE_LOGIN_EMAIL and AE_LOGIN_PASSWORD to run auth state reuse demo.");

        BrowserContext signedInContext = createContextWithSavedAuthState(email, password);
        Page signedInPage = signedInContext.newPage();
        signedInPage.navigate(APIConstants.BASE_URL);

        assertThat(signedInPage).hasURL(Pattern.compile("automationexercise\\.com/?"));
        assertThat(signedInPage.locator("a:has-text('Logged in as')")).isVisible();
        assertThat(signedInPage.locator("a:has-text('Logout')")).isVisible();

        signedInPage.close();
        signedInContext.close();
    }

    private void runLocatorAssertions() {
        page.locator("#focusInput").focus();

        assertThat(page.locator("#visibleBox")).isAttached();
        assertThat(page.locator("#agree")).isChecked();
        assertThat(page.locator("#disabledInput")).isDisabled();
        assertThat(page.locator("#editableInput")).isEditable();
        assertThat(page.locator("#emptyBox")).isEmpty();
        assertThat(page.locator("#editableInput")).isEnabled();
        assertThat(page.locator("#focusInput")).isFocused();
        assertThat(page.locator("#hiddenBox")).isHidden();
        assertThat(page.locator("#visibleBox")).isInViewport();
        assertThat(page.locator("#visibleBox")).isVisible();

        assertThat(page.locator("#visibleBox")).containsText("Welcome");
        assertThat(page.locator("#descriptionText")).containsText("easy to understand");
        assertThat(page.locator("#a11yButton")).hasAccessibleDescription("Saves the form");
        assertThat(page.locator("#a11yButton")).hasAccessibleName("Save changes");
        assertThat(page.locator("#a11yButton")).hasAttribute("data-guide", "primary");
        assertThat(page.locator("#visibleBox")).hasClass(Pattern.compile(".*card.*active.*"));
        assertThat(page.locator(".item")).hasCount(3);
        assertThat(page.locator("#visibleBox")).hasCSS("display", "block");
        assertThat(page.locator("#visibleBox")).hasId("visibleBox");
        assertThat(page.locator("#visibleBox")).hasJSProperty("demoFlag", true);
        assertThat(page.locator("#a11yButton")).hasRole(AriaRole.BUTTON);
        assertThat(page.locator("#titleText")).hasText("Locator assertion demo");
        assertThat(page.locator("#editableInput")).hasValue("Playwright");
        assertThat(page.locator("#multiSelect")).hasValues(new String[]{"dog", "cat"});
    }

    private void runResponseAssertion() {
        APIResponse response = request.get(APIConstants.PRODUCTS_ENDPOINT);
        assertThat(response).isOK();
    }

    private BrowserContext createContextWithSavedAuthState(String email, String password) throws Exception {
        Path statePath = Paths.get("target", "auth", "automationexercise-storage-state.json");
        Files.createDirectories(statePath.getParent());

        if (!Files.exists(statePath)) {
            BrowserContext authContext = browser.newContext();
            Page authPage = authContext.newPage();
            authPage.navigate(APIConstants.BASE_URL + "/login");
            authPage.locator("input[data-qa='login-email']").fill(email);
            authPage.locator("input[data-qa='login-password']").fill(password);
            authPage.locator("button[data-qa='login-button']").click();
            assertThat(authPage.locator("a:has-text('Logged in as')")).isVisible();
            authContext.storageState(new BrowserContext.StorageStateOptions().setPath(statePath));
            authPage.close();
            authContext.close();
        }

        return browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void loadAssertionsDemoPage() {
        page.setContent(
                "<html>" +
                "<head><title>Assertion Demo Page</title></head>" +
                "<body style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "  <h2 id='titleText'>Locator assertion demo</h2>" +
                "  <div id='visibleBox' class='card active' style='display:block;'>Welcome to guide</div>" +
                "  <div id='hiddenBox' style='display:none;'>Hidden element</div>" +
                "  <div id='emptyBox'></div>" +
                "  <input id='editableInput' value='Playwright'>" +
                "  <input id='disabledInput' value='Disabled' disabled>" +
                "  <input id='focusInput' value='Focus field'>" +
                "  <label><input id='agree' type='checkbox' checked> Agree</label>" +
                "  <p id='descriptionText'>This page is simple and easy to understand.</p>" +
                "  <button id='a11yButton' data-guide='primary' aria-label='Save changes' aria-description='Saves the form'>Save</button>" +
                "  <ul><li class='item'>One</li><li class='item'>Two</li><li class='item'>Three</li></ul>" +
                "  <select id='multiSelect' multiple>" +
                "    <option value='dog' selected>Dog</option>" +
                "    <option value='cat' selected>Cat</option>" +
                "    <option value='bird'>Bird</option>" +
                "  </select>" +
                "  <script>" +
                "    document.getElementById('visibleBox').demoFlag = true;" +
                "  </script>" +
                "</body>" +
                "</html>"
        );
    }
}
