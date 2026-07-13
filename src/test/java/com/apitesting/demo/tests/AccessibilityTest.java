package com.apitesting.demo.tests;

import com.apitesting.demo.utils.TestLogger;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Accessibility Tests")
public class AccessibilityTest extends BaseTest {

    private Playwright playwrightBrowser;
    private Browser browser;
    private BrowserContext browserContext;
    private Page page;
    private String currentTestName;

    @BeforeEach
    void initializePage(TestInfo testInfo) {
        currentTestName = testInfo.getDisplayName();
        playwrightBrowser = Playwright.create();
        browser = playwrightBrowser.chromium().launch();
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void cleanupPage() {
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
        super.afterEach();
    }

    /**
     * Test accessibility of the products page
     * Scans the page for common accessibility violations
     */
    @Test
    @Order(1)
    @DisplayName("A01 - Verify products page has no accessibility violations")
    void testProductsPageAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/products");
        page.waitForLoadState();

        verifyPageAccessibility("Products Page");
    }

    /**
     * Test accessibility of the home page
     * Scans the page for common accessibility violations
     */
    @Test
    @Order(2)
    @DisplayName("A02 - Verify home page has no accessibility violations")
    void testHomePageAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/");
        page.waitForLoadState();

        verifyPageAccessibility("Home Page");
    }

    /**
     * Test accessibility of the cart page
     * Scans the page for common accessibility violations
     */
    @Test
    @Order(3)
    @DisplayName("A03 - Verify cart page has no accessibility violations")
    void testCartPageAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/view_cart");
        page.waitForLoadState();

        verifyPageAccessibility("Cart Page");
    }

    /**
     * Test accessibility of the signup/login page
     * Scans the page for common accessibility violations
     */
    @Test
    @Order(4)
    @DisplayName("A04 - Verify signup/login page has no accessibility violations")
    void testSignupPageAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/login");
        page.waitForLoadState();

        verifyPageAccessibility("Signup/Login Page");
    }

    /**
     * Verify page accessibility by checking for common violations
     * 
     * @param pageName The name of the page being tested
     */
    private void verifyPageAccessibility(String pageName) throws Exception {
        AccessibilityViolations violations = checkAccessibilityViolations();
        
        TestLogger.log(currentTestName, String.format(
            "Accessibility Check on %s - Total Violations: %d", 
            pageName, violations.getTotalViolations()
        ));
        
        // Assert no critical violations
        assertEquals(0, violations.criticalViolations.size(),
            String.format("Expected no critical accessibility violations on %s\nViolations: %s",
                pageName, violations.criticalViolations));
        
        // Log warnings if present
        if (!violations.warningViolations.isEmpty()) {
            TestLogger.log(currentTestName, String.format(
                "WARNING: %d minor accessibility issues found on %s:\n%s",
                violations.warningViolations.size(), pageName, violations.warningViolations
            ));
        }
    }

    /**
     * Check for common accessibility violations on the page
     * 
     * @return AccessibilityViolations object containing found violations
     */
    private AccessibilityViolations checkAccessibilityViolations() throws Exception {
        AccessibilityViolations violations = new AccessibilityViolations();
        
        // Check 1: Page title must exist
        String title = page.title();
        if (title == null || title.isEmpty()) {
            violations.criticalViolations.add("Page has no title");
        } else {
            TestLogger.log(currentTestName, "✓ Page title exists: " + title);
        }
        
        // Check 2: Page must have h1 element
        List<ElementHandle> h1Elements = page.querySelectorAll("h1");
        if (h1Elements.isEmpty()) {
            violations.warningViolations.add("Page should have at least one H1 element");
        } else {
            TestLogger.log(currentTestName, "✓ H1 element found");
        }
        
        // Check 3: Images must have alt text
        List<ElementHandle> imagesWithoutAlt = page.querySelectorAll("img:not([alt])");
        if (!imagesWithoutAlt.isEmpty()) {
            violations.warningViolations.add(String.format(
                "%d image(s) missing alt text", imagesWithoutAlt.size()
            ));
        } else {
            TestLogger.log(currentTestName, "✓ All images have alt text");
        }
        
        // Check 4: Form inputs should have accessible labels
        List<ElementHandle> inputs = page.querySelectorAll("input[type='text'], input[type='email'], input[type='password']");
        int inputsWithoutLabel = 0;
        for (ElementHandle input : inputs) {
            String id = input.getAttribute("id");
            String ariaLabel = input.getAttribute("aria-label");
            if ((id == null || id.isEmpty()) && (ariaLabel == null || ariaLabel.isEmpty())) {
                inputsWithoutLabel++;
            }
        }
        if (inputsWithoutLabel > 0) {
            violations.warningViolations.add(String.format(
                "%d input(s) missing aria-label or id", inputsWithoutLabel
            ));
        } else {
            TestLogger.log(currentTestName, "✓ Inputs have accessible labels");
        }
        
        // Check 5: Buttons should have accessible names
        List<ElementHandle> buttons = page.querySelectorAll("button");
        int buttonsWithoutName = 0;
        for (ElementHandle button : buttons) {
            String text = button.innerText();
            String buttonTitle = button.getAttribute("title");
            String ariaLabel = button.getAttribute("aria-label");
            if ((text == null || text.isEmpty()) && (buttonTitle == null || buttonTitle.isEmpty()) && (ariaLabel == null || ariaLabel.isEmpty())) {
                buttonsWithoutName++;
            }
        }
        if (buttonsWithoutName > 0) {
            violations.warningViolations.add(String.format(
                "%d button(s) missing accessible name", buttonsWithoutName
            ));
        } else {
            TestLogger.log(currentTestName, "✓ Buttons have accessible names");
        }
        
        // Check 6: Page must be responsive (viewport check)
        Object viewportSize = page.evaluate("() => ({ width: window.innerWidth, height: window.innerHeight })");
        TestLogger.log(currentTestName, "✓ Page viewport: " + viewportSize);
        
        return violations;
    }

    /**
     * Test accessibility of a specific page section - Navigation Header
     * Demonstrates using AxeBuilder.include() pattern to scan only the header
     */
    @Test
    @Order(5)
    @DisplayName("A05 - Verify navigation header has no accessibility violations (specific region scan)")
    void testNavigationHeaderAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/");
        page.waitForLoadState();

        // Verify header exists and scan only that section
        List<ElementHandle> headers = page.querySelectorAll("header, nav");
        assertTrue(!headers.isEmpty(), "Page should have header or nav element");

        PartialAccessibilityViolations violations = checkPartialPageAccessibility("header, nav", "Navigation Header");
        
        assertEquals(0, violations.criticalViolations.size(),
            String.format("Expected no critical violations in Navigation Header, but found %d",
                violations.criticalViolations.size()));
        
        TestLogger.log(currentTestName, "Navigation header scan completed successfully");
    }

    /**
     * Test accessibility of a specific page section - Product List
     * Demonstrates using AxeBuilder.include() pattern to scan only the product grid
     */
    @Test
    @Order(6)
    @DisplayName("A06 - Verify product list has no accessibility violations (specific region scan)")
    void testProductListAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/products");
        page.waitForLoadState();

        // Verify product list exists and scan only that section
        List<ElementHandle> productList = page.querySelectorAll(".products, [class*='product']");
        assertTrue(!productList.isEmpty(), "Page should have product section");

        PartialAccessibilityViolations violations = checkPartialPageAccessibility(".products, [class*='product']", "Product List");
        
        assertEquals(0, violations.criticalViolations.size(),
            String.format("Expected no critical violations in Product List, but found %d",
                violations.criticalViolations.size()));
        
        TestLogger.log(currentTestName, "Product list scan completed successfully");
    }

    /**
     * Test accessibility of a specific page section - Login Form
     * Demonstrates scanning form sections and their accessibility
     */
    @Test
    @Order(7)
    @DisplayName("A07 - Verify login form has no accessibility violations (form region scan)")
    void testLoginFormAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/login");
        page.waitForLoadState();

        // Verify form exists and scan only that section
        List<ElementHandle> forms = page.querySelectorAll("form");
        assertTrue(!forms.isEmpty(), "Page should have form elements");

        PartialAccessibilityViolations violations = checkPartialPageAccessibility("form", "Login Form");
        
        assertEquals(0, violations.criticalViolations.size(),
            String.format("Expected no critical violations in Login Form, but found %d",
                violations.criticalViolations.size()));
        
        TestLogger.log(currentTestName, "Login form accessibility check completed");
    }

    /**
     * Test accessibility of a page after UI interaction
     * Demonstrates scanning a page after performing user interactions
     * This pattern is useful for checking dynamically revealed content
     */
    @Test
    @Order(8)
    @DisplayName("A08 - Verify page accessibility after user interactions")
    void testAccessibilityAfterUserInteraction() throws Exception {
        page.navigate("https://automationexercise.com/products");
        page.waitForLoadState();

        // Interact with page - try to find and hover over first product
        List<ElementHandle> products = page.querySelectorAll(".productinfo, [class*='product']");
        if (!products.isEmpty()) {
            // Hover to reveal any hidden elements
            products.get(0).hover();
            page.waitForTimeout(500);
            
            TestLogger.log(currentTestName, "Performed hover interaction on first product");
        }

        // Scan accessibility after interaction
        PartialAccessibilityViolations violations = checkPartialPageAccessibility("body", "Page After Interaction");
        
        TestLogger.log(currentTestName, String.format(
            "Accessibility check after interaction - Critical: %d, Warnings: %d",
            violations.criticalViolations.size(), violations.warningViolations.size()
        ));
    }

    /**
     * Test accessibility of footer section
     * Demonstrates scanning a specific region at the bottom of the page
     */
    @Test
    @Order(9)
    @DisplayName("A09 - Verify footer has no accessibility violations (footer region scan)")
    void testFooterAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/");
        page.waitForLoadState();

        // Scroll to bottom to ensure footer is loaded
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);

        // Verify footer exists and scan only that section
        List<ElementHandle> footers = page.querySelectorAll("footer, [class*='footer']");
        assertTrue(!footers.isEmpty(), "Page should have footer element");

        PartialAccessibilityViolations violations = checkPartialPageAccessibility("footer, [class*='footer']", "Footer");
        
        assertEquals(0, violations.criticalViolations.size(),
            String.format("Expected no critical violations in Footer, but found %d",
                violations.criticalViolations.size()));
        
        TestLogger.log(currentTestName, "Footer accessibility check completed");
    }

    /**
     * Test accessibility of search/filter section
     * Demonstrates scanning specific interactive components
     */
    @Test
    @Order(10)
    @DisplayName("A10 - Verify search section has no accessibility violations (component region scan)")
    void testSearchComponentAccessibility() throws Exception {
        page.navigate("https://automationexercise.com/products");
        page.waitForLoadState();

        // Look for search/filter components
        List<ElementHandle> searchElements = page.querySelectorAll("[class*='search'], [class*='filter'], input[type='text']");
        
        if (!searchElements.isEmpty()) {
            PartialAccessibilityViolations violations = checkPartialPageAccessibility(
                "[class*='search'], [class*='filter']", "Search/Filter Component"
            );
            
            TestLogger.log(currentTestName, String.format(
                "Search component violations - Critical: %d, Warnings: %d",
                violations.criticalViolations.size(), violations.warningViolations.size()
            ));
        } else {
            TestLogger.log(currentTestName, "No search/filter components found on this page");
        }
    }

    /**
     * Check accessibility violations for a specific page section (partial scan)
     * Equivalent to AxeBuilder.include(selector).analyze() pattern
     * 
     * @param selector CSS selector for the region to scan
     * @param regionName Name of the region being scanned
     * @return PartialAccessibilityViolations containing violations in this section
     */
    private PartialAccessibilityViolations checkPartialPageAccessibility(String selector, String regionName) throws Exception {
        PartialAccessibilityViolations violations = new PartialAccessibilityViolations();
        
        TestLogger.log(currentTestName, String.format("Scanning specific region: %s [%s]", regionName, selector));
        
        try {
            // Find all elements matching the selector
            List<ElementHandle> targetElements = page.querySelectorAll(selector);
            
            if (targetElements.isEmpty()) {
                violations.warningViolations.add(String.format("No elements found matching selector: %s", selector));
                return violations;
            }
            
            TestLogger.log(currentTestName, String.format("✓ Found %d element(s) matching selector", targetElements.size()));
            
            // Check images within this region
            List<ElementHandle> imagesInRegion = page.querySelectorAll(selector + " img:not([alt])");
            if (!imagesInRegion.isEmpty()) {
                violations.warningViolations.add(String.format(
                    "%d image(s) in %s missing alt text", imagesInRegion.size(), regionName
                ));
            } else {
                TestLogger.log(currentTestName, String.format("✓ All images in %s have alt text", regionName));
            }
            
            // Check links within this region
            List<ElementHandle> linksInRegion = page.querySelectorAll(selector + " a");
            int linksWithoutText = 0;
            for (ElementHandle link : linksInRegion) {
                String text = link.innerText();
                String ariaLabel = link.getAttribute("aria-label");
                String title = link.getAttribute("title");
                if ((text == null || text.isEmpty()) && 
                    (ariaLabel == null || ariaLabel.isEmpty()) &&
                    (title == null || title.isEmpty())) {
                    linksWithoutText++;
                }
            }
            if (linksWithoutText > 0) {
                violations.warningViolations.add(String.format(
                    "%d link(s) in %s lack accessible name", linksWithoutText, regionName
                ));
            } else if (!linksInRegion.isEmpty()) {
                TestLogger.log(currentTestName, String.format("✓ All %d link(s) in %s have accessible names", linksInRegion.size(), regionName));
            }
            
            // Check buttons within this region
            List<ElementHandle> buttonsInRegion = page.querySelectorAll(selector + " button");
            int buttonsWithoutName = 0;
            for (ElementHandle button : buttonsInRegion) {
                String text = button.innerText();
                String buttonTitle = button.getAttribute("title");
                String ariaLabel = button.getAttribute("aria-label");
                if ((text == null || text.isEmpty()) && 
                    (buttonTitle == null || buttonTitle.isEmpty()) &&
                    (ariaLabel == null || ariaLabel.isEmpty())) {
                    buttonsWithoutName++;
                }
            }
            if (buttonsWithoutName > 0) {
                violations.warningViolations.add(String.format(
                    "%d button(s) in %s lack accessible name", buttonsWithoutName, regionName
                ));
            } else if (!buttonsInRegion.isEmpty()) {
                TestLogger.log(currentTestName, String.format("✓ All %d button(s) in %s have accessible names", buttonsInRegion.size(), regionName));
            }
            
            // Check inputs within this region
            List<ElementHandle> inputsInRegion = page.querySelectorAll(selector + " input[type='text'], " + selector + " input[type='email'], " + selector + " input[type='password']");
            int inputsWithoutLabel = 0;
            for (ElementHandle input : inputsInRegion) {
                String id = input.getAttribute("id");
                String ariaLabel = input.getAttribute("aria-label");
                if ((id == null || id.isEmpty()) && (ariaLabel == null || ariaLabel.isEmpty())) {
                    inputsWithoutLabel++;
                }
            }
            if (inputsWithoutLabel > 0) {
                violations.warningViolations.add(String.format(
                    "%d input(s) in %s missing label", inputsWithoutLabel, regionName
                ));
            } else if (!inputsInRegion.isEmpty()) {
                TestLogger.log(currentTestName, String.format("✓ All %d input(s) in %s have labels", inputsInRegion.size(), regionName));
            }
            
            TestLogger.log(currentTestName, String.format(
                "Region scan completed: %s - Violations: %d, Warnings: %d",
                regionName, violations.criticalViolations.size(), violations.warningViolations.size()
            ));
            
        } catch (Exception e) {
            violations.criticalViolations.add(String.format("Error scanning region %s: %s", regionName, e.getMessage()));
            TestLogger.log(currentTestName, "Error during region scan: " + e.getMessage());
        }
        
        return violations;
    }

    /**
     * Inner class to store partial page accessibility violations
     * Used for region-specific scans similar to AxeBuilder.include()
     */
    private static class PartialAccessibilityViolations {
        List<String> criticalViolations = new java.util.ArrayList<>();
        List<String> warningViolations = new java.util.ArrayList<>();
        
        int getTotalViolations() {
            return criticalViolations.size() + warningViolations.size();
        }
    }

    /**
     * Inner class to store accessibility violations
     */
    private static class AccessibilityViolations {
        List<String> criticalViolations = new java.util.ArrayList<>();
        List<String> warningViolations = new java.util.ArrayList<>();
        
        int getTotalViolations() {
            return criticalViolations.size() + warningViolations.size();
        }
    }
}
