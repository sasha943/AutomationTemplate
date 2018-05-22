package com.qa.utils.webDriverUtils;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class WebDriversCollection extends Thread{

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriversCollection.class);

    private static ConcurrentHashMap concurrentMap = new ConcurrentHashMap();

    public static void addWebDriver(String session, WebDriver webDriver) {
        LOGGER.info("-> WebDriversCollection: added webDriver, session: " + session);
        concurrentMap.putIfAbsent(session, webDriver);
    }

    public static ConcurrentHashMap getConcurrentMap() {
        return concurrentMap;
    }

}
