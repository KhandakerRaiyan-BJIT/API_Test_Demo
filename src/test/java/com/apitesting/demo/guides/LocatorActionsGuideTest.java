package com.apitesting.demo.guides;

import com.apitesting.demo.tests.BaseTest;
import com.apitesting.demo.utils.TestLogger;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@DisplayName("Locator Actions Guide")
public class LocatorActionsGuideTest extends BaseTest {

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
        browserContext = browser.newContext(new Browser.NewContextOptions().setHasTouch(true));
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
    @DisplayName("L01 - Demonstrate common locator actions")
    void demonstrateLocatorActions() throws Exception {
        loadDemoPage();

        Locator nameInput = page.locator("#nameInput");
        Locator noteInput = page.locator("#noteInput");
        Locator clickButton = page.locator("#clickButton");
        Locator doubleClickButton = page.locator("#doubleClickButton");
        Locator tapButton = page.locator("#tapButton");
        Locator hoverBox = page.locator("#hoverBox");
        Locator dragSource = page.locator("#dragSource");
        Locator dropTarget = page.locator("#dropTarget");
        Locator selectBox = page.locator("#fruitSelect");
        Locator textArea = page.locator("#notes");

        nameInput.fill("Playwright");
        assertEquals("Playwright", nameInput.inputValue());

        nameInput.clear();
        assertEquals("", nameInput.inputValue());

        noteInput.fill("Keep this text");
        assertEquals("Keep this text", noteInput.inputValue());

        page.locator("#checkBoxCheck").check();
        assertTrue(page.locator("#checkBoxCheck").isChecked(), "check() should select the checkbox");

        page.locator("#setCheckBox").setChecked(true);
        assertTrue(page.locator("#setCheckBox").isChecked(), "setChecked(true) should select the checkbox");

        page.locator("#uncheckBox").uncheck();
        assertTrue(!page.locator("#uncheckBox").isChecked(), "uncheck() should clear the checkbox");

        page.locator("#radioOne").check();
        assertTrue(page.locator("#radioOne").isChecked(), "check() should select the radio button");

        clickButton.click();
        assertEquals("Clicked 1 time(s)", page.locator("#status").innerText());

        doubleClickButton.dblclick();
        assertEquals("Double clicked", page.locator("#status").innerText());

        tapButton.tap();
        assertEquals("Tapped", page.locator("#status").innerText());

        hoverBox.hover();
        assertEquals("Hovered", page.locator("#hoverStatus").innerText());

        dragSource.dragTo(dropTarget);
        assertEquals("Dropped", dropTarget.innerText());

        selectBox.selectOption("banana");
        assertEquals("banana", selectBox.inputValue());

        textArea.selectText();
        Map<?, ?> selection = (Map<?, ?>) textArea.evaluate(
                "element => ({ start: element.selectionStart, end: element.selectionEnd, length: element.value.length })"
        );
        assertEquals(0, ((Number) selection.get("start")).intValue());
        assertEquals(((Number) selection.get("length")).intValue(), ((Number) selection.get("end")).intValue());

        Path screenshotPath = Paths.get("target", "locator-actions-guide.png");
        Files.createDirectories(screenshotPath.getParent());
        hoverBox.screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));
        assertTrue(Files.exists(screenshotPath), "Locator screenshot should be created");

        TestLogger.log(currentTestName, "Locator actions guide completed successfully");
    }

    private void loadDemoPage() {
        page.setContent(
                "<html>" +
                "<body style='font-family: Arial, sans-serif; padding: 24px;'>" +
                "  <h2>Locator actions demo</h2>" +
                "  <input id='nameInput' type='text' value=''>" +
                "  <input id='noteInput' type='text' value='Demo note'>" +
                "  <label><input id='checkBoxCheck' type='checkbox'> Check me</label>" +
                "  <label><input id='setCheckBox' type='checkbox'> Set me</label>" +
                "  <label><input id='uncheckBox' type='checkbox' checked> Uncheck me</label>" +
                "  <label><input id='radioOne' type='radio' name='choice'> Radio one</label>" +
                "  <label><input id='radioTwo' type='radio' name='choice'> Radio two</label>" +
                "  <div style='margin-top: 12px;'>" +
                "    <button id='clickButton' type='button'>Click</button>" +
                "    <button id='doubleClickButton' type='button'>Double click</button>" +
                "    <button id='tapButton' type='button'>Tap</button>" +
                "  </div>" +
                "  <div id='hoverBox' style='margin-top: 12px; width: 180px; padding: 16px; background: #eef; border: 1px solid #99c;'>Hover box</div>" +
                "  <div id='dragSource' draggable='true' style='margin-top: 12px; width: 120px; padding: 12px; background: #fee; border: 1px solid #c99;'>Drag me</div>" +
                "  <div id='dropTarget' style='margin-top: 12px; width: 120px; min-height: 40px; padding: 12px; background: #efe; border: 1px dashed #9c9;'>Drop here</div>" +
                "  <select id='fruitSelect' style='margin-top: 12px;'>" +
                "    <option value='apple'>Apple</option>" +
                "    <option value='banana'>Banana</option>" +
                "    <option value='orange'>Orange</option>" +
                "  </select>" +
                "  <textarea id='notes' style='display:block; margin-top: 12px; width: 240px; height: 80px;'>Select this text</textarea>" +
                "  <div id='status' style='margin-top: 12px;'>Ready</div>" +
                "  <div id='hoverStatus' style='margin-top: 8px;'>Not hovered</div>" +
                "  <script>" +
                "    let clickCount = 0;" +
                "    const status = document.getElementById('status');" +
                "    document.getElementById('clickButton').addEventListener('click', () => { clickCount++; status.textContent = 'Clicked ' + clickCount + ' time(s)'; });" +
                "    document.getElementById('doubleClickButton').addEventListener('dblclick', () => { status.textContent = 'Double clicked'; });" +
                "    document.getElementById('tapButton').addEventListener('click', () => { status.textContent = 'Tapped'; });" +
                "    document.getElementById('hoverBox').addEventListener('mouseenter', () => { document.getElementById('hoverStatus').textContent = 'Hovered'; });" +
                "    const dropTarget = document.getElementById('dropTarget');" +
                "    dropTarget.addEventListener('dragover', event => event.preventDefault());" +
                "    dropTarget.addEventListener('drop', event => { event.preventDefault(); dropTarget.textContent = 'Dropped'; });" +
                "  </script>" +
                "</body>" +
                "</html>"
        );
    }
}
