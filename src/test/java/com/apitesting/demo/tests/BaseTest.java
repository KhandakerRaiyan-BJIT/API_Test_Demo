package com.apitesting.demo.tests;

import com.apitesting.demo.utils.APIConstants;
import com.apitesting.demo.utils.TestLogger;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.lang.reflect.Method;

public abstract class BaseTest {
    protected static Playwright playwright;
    protected static APIRequestContext request;
    private static final ThreadLocal<String> CURRENT_TEST_NAME = new ThreadLocal<>();

    @BeforeAll
    static void setUp() {
        TestLogger.initialize();
        playwright = Playwright.create();
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(APIConstants.BASE_URL)
        );
        TestLogger.log("SUITE", "Base URL: " + APIConstants.BASE_URL);
        TestLogger.log("SUITE", "Log file: " + TestLogger.logPath());
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        String testName = testInfo.getDisplayName();
        CURRENT_TEST_NAME.set(testName);
        TestLogger.log(testName, "START");
    }

    @AfterEach
    void afterEach() {
        String testName = currentTestName();
        TestLogger.log(testName, "END");
        attachExecutionLogToReport();
    }

    @AfterAll
    static void tearDown() {
        if (request != null) {
            request.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    protected void logResponse(String endpoint, int statusCode, String responseBody) {
        String preview = responseBody.length() > 700 ? responseBody.substring(0, 700) + "..." : responseBody;
        TestLogger.log(currentTestName(), "Endpoint: " + endpoint + " | Status: " + statusCode);
        TestLogger.log(currentTestName(), "Response body: " + preview);
    }

    private String currentTestName() {
        String testName = CURRENT_TEST_NAME.get();
        return testName == null ? "UNKNOWN_TEST" : testName;
    }

    private void attachExecutionLogToReport() {
        try {
            Class<?> allureClass = Class.forName("io.qameta.allure.Allure");
            Method addAttachment = allureClass.getMethod(
                    "addAttachment",
                    String.class,
                    String.class,
                    String.class,
                    String.class
            );
            addAttachment.invoke(null, "Execution log", "text/plain", TestLogger.readAll(), ".log");
        } catch (Exception ignored) {
            // Keep test execution stable even when Allure classes are unavailable in IDE/runtime.
        }
    }
}
