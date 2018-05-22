package com.qa.pageObjectPattern.basePages;

import com.codeborne.selenide.*;
import com.qa.utils.SysUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;

public class BasePageImpl implements BasePage {

    private static Logger LOG = LoggerFactory.getLogger(BasePageImpl.class);

    private WebDriver webDriver;

    public BasePageImpl(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    private static final int DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_ALERT_TIMEOUT_SECONDS = 5;
    private static final int DEFAULT_AJAX_TIMEOUT_SECONDS = 15;
    private static final int DEFAULT_NEW_WINDOW_TIMEOUT_SECONDS = 15;
    private static final int DEFAULT_POLLING_EVERY_MILLISECONDS = 1000;

    /**
     * Operation with WebElements.
     */

    protected SelenideElement getWebElement(By by) {
        return $(by);
    }

    @Override
    public void click(By by) {
        try {
            getWebElement(by).click();
        } catch (UnhandledAlertException e) {
        }
    }

    protected void clickOnVisibleElement(By by) {
        ElementsCollection webElements = getWebElements(by);
        for (SelenideElement webElement : webElements) {
            if (Condition.appear.apply(webElement)) {
                webElement.click();
                break;
            }
        }
    }

    @Override
    public void submit(By by) {
        getWebElement(by).submit();
    }

    protected void moveToAndClick(By by) {
        moveTo(by);
        getElement(by).shouldBe(Condition.visible).click();
    }

    @Override
    public void clear(By by) {
        getWebElement(by).should(Condition.visible).clear();
    }

    protected void pressEnterOnElement(By by) {
        getWebElement(by).pressEnter();
    }

    @Override
    public void type(By by, CharSequence value) {
        getWebElement(by).setValue(String.valueOf(value));
    }

    @Override
    public void clearAndType(By by, CharSequence value) {
        getWebElement(by).setValue(String.valueOf(value));
    }

    @Override
    public void setSelectStatusForElement(boolean selectStatus, By by) {
        if (selectStatus) {
            if (!isSelected(by)) {
                click(by);
            }
        } else {
            if (isSelected(by)) {
                click(by);
            }
        }
    }

    @Override
    public Point getLocation(By by) {
        return getWebElement(by).getLocation();
    }

    @Override
    public String getAttribute(By by, String attribute) {
        return getWebElement(by).attr(attribute);
    }

    protected String getCssValue(By by, String attribute) {
        return getWebElement(by).getCssValue(attribute);
    }

    @Override
    public String getText(By by) {
        return getWebElement(by).text();
    }

    @Override
    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }

    @Override
    public String getTitle() {
        return Selenide.title();
    }

    @Override
    public String getValue(By by) {
        return getWebElement(by).getValue();
    }

    @Override
    public boolean isSelected(By by) {
        return getWebElement(by).isSelected();
    }

    protected boolean isAttributeChecked(By by) {
        return getWebElement(by).is(Condition.checked);
    }

    protected boolean isAttributeReadOnly(By by) {
        return getWebElement(by).is(Condition.readonly);
    }

    protected boolean isAttributeDisabled(By by) {
        return getWebElement(by).is(Condition.disabled);
    }

    @Override
    public boolean isTextPresent(String text) {
        try {
            return isElementDisplayed(By.xpath("//*[contains(text(),'" + text + "')]"));
        } catch (Exception e) {
            return false;
        }
    }

    protected int getHeight(By by) {
        return getWebElement(by).getSize().getHeight();
    }

    protected int getWidth(By by) {
        return getWebElement(by).getSize().getWidth();
    }

    /**
     * Operation with List of WebElements.
     */

    protected ElementsCollection getWebElements(By by) {
        return $$(by);
    }

    private SelenideElement getWebElement(By by, int num) {
        return getWebElements(by).get(num);
    }

    @Override
    public void click(By by, int item) {
        waitForElementDisplayed(by, item);
        getWebElement(by, item).click();
    }

    @Override
    public void type(By by, int index, CharSequence value) {
        getWebElement(by, index).sendKeys(value);
    }

    @Override
    public void clearAndType(By by, int index, String value) {
        getWebElement(by, index).setValue(value);
    }

    @Override
    public int getSize(By by) {
        return getWebElements(by).size();
    }

    @Override
    public String getText(By by, int item) {
        return getWebElement(by, item).getText();
    }

    @Override
    public String getAttribute(By by, int item, String attribute) {
        return getWebElement(by, item).getAttribute(attribute);
    }

    protected String getCssValue(By by, int item, String attribute) {
        return getWebElement(by, item).getCssValue(attribute);
    }

    protected boolean isAttributeDisabled(By by, int num) {
        return getWebElement(by, num).is(Condition.disabled);
    }

    protected List<String> getElementsCssValue(By by, String attribute) {
        List<String> optionAttributes = new ArrayList<>();
        optionAttributes.addAll(getWebElements(by).stream().map(webElement -> webElement.getCssValue(attribute)).collect(Collectors.toList()));
        return optionAttributes;
    }

    @Override
    public List<String> getElementsText(By by) {
        List<String> optionText = new ArrayList<>();
        ElementsCollection options = getWebElements(by);
        optionText.addAll(options.stream().map(WebElement::getText).collect(Collectors.toList()));
        return optionText;
    }

    protected List<String> getElementsAttribute(By by, String attribute) {
        List<String> optionsAttribute = new ArrayList<>();
        ElementsCollection options = getWebElements(by);
        optionsAttribute.addAll(options.stream().map(webElement -> webElement.attr(attribute)).collect(Collectors.toList()));
        return optionsAttribute;
    }

    @Override
    public boolean isSelected(By by, int num) {
        return getWebElement(by, num).isSelected();
    }

    /**
     * Operation with Select.
     */

    private Select getSelect(By by) {
        return new Select(getWebElement(by));
    }

    private Select getSelect(By by, int num) {
        return new Select(getWebElement(by, num));
    }

    @Override
    public void selectByIndex(By by, int index) {
        Select select = getSelect(by);
        select.selectByIndex(index);
    }

    @Override
    public void selectByVisibleText(By by, String text) {
        Select select = getSelect(by);
        select.selectByVisibleText(text);
    }

    @Override
    public void selectByVisibleText(By by, int num, String text) {
        Select select = getSelect(by, num);
        select.selectByVisibleText(text);
    }

    protected void selectVisibleElementByVisibleText(By locator, String visibleValue) {
        ElementsCollection webElements = getWebElements(locator);
        for (SelenideElement webElement : webElements) {
            if (webElement.is(Condition.visible)) {
                new Select(webElement).selectByVisibleText(visibleValue);
                break;
            }
        }
    }

    @Override
    public void selectByValue(By by, String value) {
        Select select = getSelect(by);
        select.selectByValue(value);
    }

    @Override
    public void selectByValue(By by, int num, String value) {
        Select select = getSelect(by, num);
        select.selectByValue(value);
    }

    protected void deselectByIndex(By by, int index) {
        Select select = getSelect(by);
        select.deselectByIndex(index);
    }

    protected void deselectAll(By by) {
        Select select = getSelect(by);
        select.deselectAll();
    }

    protected int getOptionSize(By by) {
        return getSelect(by).getOptions().size();
    }

    protected int getAllSelectedOptionSize(By by) {
        return getSelect(by).getAllSelectedOptions().size();
    }

    protected String getSelectedOptionText(By by, int index) {
        return getSelect(by).getAllSelectedOptions().get(index).getText();
    }

    protected String getOptionText(By by, int index) {
        return getSelect(by).getOptions().get(index).getText();
    }

    protected List<String> getOptionsText(By by) {
        List<String> optionsText = new ArrayList<>();
        int size = getOptionSize(by);
        for (int i = 0; i < size; i++) {
            optionsText.add(getOptionText(by, i));
        }
        return optionsText;
    }

    /**
     * Operation with Actions.
     */

    protected void clickByAction(By by) {
        Selenide.actions().keyDown(Keys.CONTROL).
                click(getWebElement(by)).
                keyUp(Keys.CONTROL).
                perform();
    }

    protected void clickAndHold(By by) {
        WebElement element = getWebElement(by);
        Selenide.actions().clickAndHold(element).perform();
    }

    protected void clickAndHold(By by, int element) {
        WebElement targetElement = getWebElement(by, element);
        Selenide.actions().clickAndHold(targetElement).perform();
    }

    protected void doubleClick(By by) {
        getWebElement(by).doubleClick();
    }

    protected void moveToElementWithRelease(By by, int element) {
        WebElement targetElement = getWebElement(by, element);
        Selenide.actions().moveToElement(targetElement).release(targetElement).build().perform();
    }

    protected void moveToElementWithRelease(By by) {
        WebElement element = getWebElement(by);
        Selenide.actions().moveToElement(element).release(element).build().perform();
    }

    @Override
    public void moveTo(By by) {
        SelenideElement webElement = getWebElement(by);
        Selenide.actions().moveToElement(webElement).build().perform();
    }

    protected void moveToTheOneOfTheElements(By by, int item) {
        WebElement element = getWebElement(by, item);
        Selenide.actions().moveToElement(element).build().perform();
    }

    protected void moveByOffset(By by) {
        WebElement element = getWebElement(by);
        Selenide.actions().moveByOffset(0, element.getLocation().getY()).perform();
    }

    protected void contextClick(By by) {
        getWebElement(by).contextClick();
    }

    protected void dragAndDrop(By source, By target) {
        getWebElement(source).dragAndDropTo(getWebElement(target));
    }

    @Override
    public boolean isElementDisplayed(By by, int num) {
        return getWebElement(by, num).is(Condition.visible);
    }

    @Override
    public void waitForElementNotVisible(final By locator, int index) {
        getWebElement(locator, index).waitUntil(Condition.not(Condition.visible), DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS);
    }

    private void waitForAttributeNotEmpty(final By locator, String attribute) {
        Selenide.Wait().withMessage("Attribute " + locator + " is still not present or empty.")
                .until(ExpectedConditions.attributeToBeNotEmpty(getWebElement(locator), attribute));
    }

    protected void waitForValueAttributeNotEmpty(final By locator) {
        waitForAttributeNotEmpty(locator, "value");
    }

    private boolean isDisplayed(By by) throws NoSuchElementException, NullPointerException, StaleElementReferenceException {
        return getWebElement(by).is(Condition.visible);
    }

    @Override
    public boolean isElementDisplayed(By by) {
        return getWebElement(by).isDisplayed();
    }

    protected boolean isElementDisplayedMobile(By by) {
        LOG.debug("Temporary circumvention for Appium bug #6476, #9324.");
        try {
            return isDisplayed(by);
        } catch (WebDriverException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public Alert waitForAlert(int seconds) {
        Alert alert = null;
        try {
            alert = Selenide.Wait().ignoring(UnhandledAlertException.class)
                    .withMessage("Alert does not appear in " + seconds + " seconds.")
                    .until(ExpectedConditions.alertIsPresent());
        } catch (UnhandledAlertException e) {
        }
        LOG.info("ALERT displayed. Alert text '" + alert.getText() + "'.");
        return alert;
    }

    @Override
    public void takeConfirmIfPresent() {
        try {
            takeConfirm();
        } catch (NoAlertPresentException | TimeoutException e) {
            LOG.info("Alert does not appear.");
        }
    }

    protected void takeConfirmIfPresent(String expectedAlertText) {
        try {
            Selenide.confirm(expectedAlertText);
        } catch (NoAlertPresentException | TimeoutException e) {
            LOG.info("Alert does not appear.");
        }
    }

    @Override
    public void takeConfirm() {
        waitForAlert(DEFAULT_ALERT_TIMEOUT_SECONDS).accept();
    }

    @Override
    public void takeConfirm(String expectedAlertText) {
        waitForAlert(DEFAULT_ALERT_TIMEOUT_SECONDS);
        Selenide.confirm(expectedAlertText);
    }

    @Override
    public void waitForPageLoaded() {
        Selenide.Wait().withTimeout(60, TimeUnit.SECONDS).withMessage("Timeout waiting for Page Load Request to complete.")
                .until(result -> Selenide.executeJavaScript("return document.readyState").equals("complete"));
    }

    @Override
    public void waitForAjax(int timeoutInSeconds) {
        LOG.info("Wait for ajax ..");
        Selenide.Wait().withTimeout(timeoutInSeconds, TimeUnit.SECONDS).withMessage("Ajax not finished yet.")
                .until(result -> (Boolean) Selenide.executeJavaScript("return (window.jQuery != null) && (jQuery.active == 0)"));
    }

    @Override
    public void waitForAjax() {
        waitForAjax(DEFAULT_AJAX_TIMEOUT_SECONDS);
    }

    @Override
    public void waitForFinishResizePopup() {
        Selenide.Wait().withMessage("Popup resizing not finished.")
                .until(result -> ((Long) executeJavaScript("return document.getElementsByClassName('ui_FinishResizePopup').length") == 1));
    }

    @Override
    public void waitForElementDisplayed(final By locator) {
        waitForElementDisplayedByTime(locator, DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS);
    }

    @Override
    public void waitForElementDisplayedByTime(final By locator, final long time) {
        getWebElement(locator).waitUntil(Condition.visible, time);
    }

    @Override
    public void waitForElementDisplayed(final By locator, final int item) {
        getWebElement(locator, item).waitUntil(Condition.visible, 60);
    }

    protected void waitForDisplayedAtLeastOneOfTheElements(final By locator, final long timeInSeconds) {
        Selenide.Wait().withTimeout(timeInSeconds, TimeUnit.MILLISECONDS)
                .withMessage("Any element of elements '" + locator.toString() + "' is not displayed.")
                .until((WebDriver result) -> {
                    for (SelenideElement webElement : getWebElements(locator)) {
                        if (webElement.isDisplayed()) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    protected void waitForDisplayedAtLeastOneOfTheElements(final By locator) {
        waitForDisplayedAtLeastOneOfTheElements(locator, DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS);
    }

    protected void waitForDisplaySpecificNumberOfElements(final By locator, final int expectedCount) {
        Selenide.Wait()
                .withTimeout(DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .pollingEvery(DEFAULT_POLLING_EVERY_MILLISECONDS, TimeUnit.MILLISECONDS)
                .withMessage("Number of displayed elements " + locator.toString() + " still different from " + expectedCount)
                .until(result -> {

                    ElementsCollection elements = getWebElements(locator);

                    if (elements.size() != expectedCount) {
                        LOG.info("Current elements size is: " + elements.size());
                        return Boolean.FALSE;
                    }

                    for (SelenideElement element : elements) {
                        if (!element.isDisplayed()) {
                            LOG.info("Element " + element + "is not displayed");
                            return Boolean.FALSE;
                        }
                    }
                    return Boolean.TRUE;
                });
    }

    protected void waitForSpecificNumberOfElements(final By locator, final int expectedCount) {
        Selenide.Wait().withTimeout(DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .withMessage("Number of elements " + locator.toString() + " still different from " + expectedCount)
                .until(ExpectedConditions.numberOfElementsToBe(locator, expectedCount));
    }

    @Override
    public void waitForElementNotVisible(final By locator) {
        waitForElementNotVisibleByTime(locator, DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS);
    }

    protected void waitForElementNotVisibleByTime(final By locator, long seconds) {
        Selenide.Wait().withTimeout(seconds, TimeUnit.SECONDS)
                .withMessage("Element " + locator + " is still visible.")
                .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForElementWithRefresh(By by) {
        for (int i = 0; i < 10; i++) {
            if (isElementDisplayed(by)) {
                break;
            } else {
                SysUtils.sleep(2000);
                refreshPage();
            }
        }
    }

    protected void waitForElementClickable(By locator) {
        getWebElement(locator).waitUntil((Condition) Condition.be(Condition.visible).and(Condition.enabled),
                DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS);
    }

    protected void waitForElementDisplayedMobile(final By locator) {
        LOG.debug("Temporary circumvention for Appium bug #6476, #9324.");
        Selenide.Wait().withTimeout(DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .ignoring(WebDriverException.class, NullPointerException.class)
                .withMessage("Element " + locator.toString() + " is still not displayed.")
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public void switchToDefaultContent() {
        Selenide.switchTo().defaultContent();
    }

    @Override
    public void switchToFrame(int frameNumber) {
        Selenide.switchTo().frame(frameNumber);
    }

    @Override
    public void switchToFrame(String frameName) {
        WebElement element = getWebElement(By.id(frameName));
        Selenide.switchTo().frame(element);
    }

    protected void switchToFrameByLocator(By by) {
        WebElement webElement = getWebElement(by);
        Selenide.switchTo().frame(webElement);
    }

    protected void switchToActiveElement() {
        Selenide.switchTo().activeElement();
    }

    protected void waitForFrameAndSwitchToIt(String frameName) {
        Selenide.Wait().withTimeout(DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .withMessage("Frame with name '" + frameName + "' does not appear in " + DEFAULT_ELEMENT_VISIBILITY_TIMEOUT_SECONDS + " seconds.")
                .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(frameName)));
    }

    @Override
    public void switchToNewWindow(String currentWindow) {
        webDriver.getWindowHandles().stream().filter(winHandle -> !winHandle.equals(currentWindow))
                .forEach(winHandle -> webDriver.switchTo().window(winHandle).manage().window().maximize());
    }

    @Override
    public void waitForNewWindow(int previousNumOfWindows, int timeOutInSeconds) {
        Selenide.Wait().withTimeout(DEFAULT_POLLING_EVERY_MILLISECONDS, TimeUnit.SECONDS)
                .withMessage("New window does not appear in " + timeOutInSeconds + " seconds.")
                .until(ExpectedConditions.numberOfWindowsToBe(previousNumOfWindows + 1));
    }

    @Override
    public void waitForSecondWindowAndSwitchToIt(String currentWindow) {
        waitForNewWindow(1, DEFAULT_NEW_WINDOW_TIMEOUT_SECONDS);
        switchToNewWindow(currentWindow);
    }

    @Override
    public void closeWindowAndSwitchToOther() {
        String currentWindow = webDriver.getWindowHandle();
        Selenide.close();
        switchToNewWindow(currentWindow);
    }

    @Override
    public void refreshPage() {
        LOG.info("Refreshing the page");
        Selenide.refresh();
        waitForPageLoaded();
    }

    @Override
    public Object executeJavaScript(String script, Object... objects) {
        if (objects == null) {
            return Selenide.executeJavaScript(script);
        } else {
            return Selenide.executeJavaScript(script, objects);
        }
    }

    protected void typeValueWithJSToFieldWithId(String id, String value) {
        executeJavaScript("document.getElementById('" + id + "').value ='" + value + "'");
    }

    protected void typeValueWithJSToFieldByXpathAndNum(By by, int num, String value) {
        SelenideElement element = getWebElements(by).get(num);
        executeJavaScript("var element=arguments[0]; element.value ='" + value + "'", element);
    }

    @Override
    public void scrollTo(By by) {
        Point point = getWebElement(by).getLocation();
        executeJavaScript("window.scrollTo(" + point.getX() + "," + (point.getY() - 200) + ");");
    }

    @Override
    public void scrollTo(By by, int num) {
        Point point = getWebElements(by).get(num).getLocation();
        executeJavaScript("window.scrollTo(" + point.getX() + "," + (point.getY() - 200) + ");");
    }

    @Override
    public void scrollToElementByCoordinates(int x, int y) {
        executeJavaScript("window.scroll(" + x + "," + y + ")");
    }

    protected void scrollIntoViewById(String elementId) {
        executeJavaScript("return document.getElementById('" + elementId + "').scrollIntoView()");
    }

    protected void scrollIntoView(By by) {
        getWebElement(by).scrollIntoView(false);
    }

    protected boolean isElementContainVerticalScrollBar(By by) {
        return (boolean) executeJavaScript("return arguments[0].scrollHeight > arguments[0].clientHeight;", getWebElement(by));
    }

    protected Object focusOnElementById(String element) {
        return Selenide.executeJavaScript("document.getElementById('" + element + "').focus()");
    }

    protected void focusOnElementWithJS(By by) {
        executeJavaScript("var elem=arguments[0]; setTimeout(function() {elem.focus();}, 100)", getWebElement(by));
    }

    protected void clickOnElementWithJS(By by) {
        executeJavaScript("var elem=arguments[0]; setTimeout(function() {elem.click();}, 100)", getWebElement(by));
    }

    protected List<String> getAttributeValueForVisibleElements(By by, String attribute) {
        List<String> attributes = new ArrayList<>();
        ElementsCollection elements = getWebElements(by);
        attributes.addAll(elements.stream().map(element -> element.getAttribute(attribute))
                .collect(Collectors.toList()));
        return attributes;
    }

    protected boolean isElementsEmpty(By by) {
        return getWebElements(by).isEmpty();
    }

    @Override
    public int getWindowHandlesSize() {
        return webDriver.getWindowHandles().size();
    }

    @Override
    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    @Override
    public void openPage(String url) {
        Selenide.open(url);
        waitForPageLoaded();
    }
}
