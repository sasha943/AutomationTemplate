package com.qa.pageObjectPattern.basePages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;

import java.util.List;

public interface IBasePage {

    void click(By by);

    void submit(By by);

    void clear(By by);

    void type(By by, CharSequence value);

    void clearAndType(By by, CharSequence value);

    void setSelectStatusForElement(boolean selectStatus, By by);

    Point getLocation(By by);

    String getAttribute(By by, String attribute);

    String getText(By by);

    String getCurrentUrl();

    String getTitle();

    String getValue(By by);

    boolean isSelected(By by);

    boolean isTextPresent(String text);

    void click(By by, int item);

    void type(By by, int index, CharSequence value);

    void clearAndType(By by, int index, String value);

    int getSize(By by);

    String getText(By by, int item);

    String getAttribute(By by, int item, String attribute);

    List<String> getElementsText(By by);

    boolean isSelected(By by, int num);

    void selectByIndex(By by, int index);

    void selectByVisibleText(By by, String text);

    void selectByVisibleText(By by, int num, String text);

    void selectByValue(By by, String value);

    void selectByValue(By by, int num, String value);

    void moveTo(By by);

    /**
     * WebElements availability.
     */

    boolean isElementDisplayed(By by, int num);

    void waitForElementNotVisible(By locator, int index);

    boolean isElementDisplayed(By by);

    /**
     * Operation with alerts.
     */

    Alert waitForAlert(int seconds);

    void takeConfirmIfPresent();

    void takeConfirm();

    void takeConfirm(String expectedAlertText);

    /**
     * Explicitly/Implicitly waits.
     */

    void waitForPageLoaded();

    void waitForAjax(int timeoutInSeconds);

    void waitForAjax();

    void waitForFinishResizePopup();

    void waitForElementDisplayed(By locator);

    void waitForElementDisplayedByTime(By locator, long time);

    void waitForElementDisplayed(By locator, int item);

    void waitForElementNotVisible(By locator);

    /**
     * Frame methods.
     */

    void switchToDefaultContent();

    void switchToFrame(int frameNumber);

    void switchToFrame(String frameName);

    void switchToNewWindow(String currentWindow);

    void waitForNewWindow(int previousNumOfWindows, int timeOutInSeconds);

    void waitForSecondWindowAndSwitchToIt(String currentWindow);

    void closeWindowAndSwitchToOther();

    /**
     * Other methods.
     */

    void refreshPage();

    Object executeJavaScript(String script, Object... objects);

    void scrollTo(By by);

    void scrollTo(By by, int num);

    void scrollToElementByCoordinates(int x, int y);

    int getWindowHandlesSize();

    String getWindowHandle();

    void openPage(String url);
}
