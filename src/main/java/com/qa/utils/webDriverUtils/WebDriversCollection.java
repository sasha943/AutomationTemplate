package com.qa.utils.webDriverUtils;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.ConcurrentHashMap;

public class WebDriversCollection extends Thread{

    private static ConcurrentHashMap concurrentMap = new ConcurrentHashMap();

    public static void addWebDriver(String session, WebDriver webDriver) {
        System.out.println("-> WebDriversCollection: added webDriver, session: " + session);
        concurrentMap.putIfAbsent(session, webDriver);
    }

    public static ConcurrentHashMap getConcurrentMap() {
        return concurrentMap;
    }

}
