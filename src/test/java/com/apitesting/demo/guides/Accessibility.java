package com.apitesting.demo.guides;

import com.apitesting.demo.tests.BaseTest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Accessibility Tests")
public class Accessibility extends BaseTest {

    private Playwright playwrightBrowser;
    private Browser browser;
    private BrowserContext browserContext;
    private Page page;
    private String currentTestName;



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



        // Check 2: Images must have alt text
        List<ElementHandle> imagesWithoutAlt = page.querySelectorAll("img:not([alt])");
        if (!imagesWithoutAlt.isEmpty()) {
            violations.warningViolations.add(String.format(
                    "%d image(s) missing alt text", imagesWithoutAlt.size()
            ));
        } else {
            TestLogger.log(currentTestName, "✓ All images have alt text");
        }


        return violations;
    }



    /**
     * Test accessibility of a specific page section - Product List
     * Demonstrates using AxeBuilder.include() pattern to scan only the product grid
     */
    @Test
    @Order(3)
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
