package com.qa.tests;

import com.qa.config.AppConfig;
import com.qa.testLink.TestLinkUtils;
import com.qa.utils.AppUtil;
import com.qa.utils.CustomWriterAppender;
import com.qa.utils.SysUtils;
import com.qa.utils.webDriverUtils.WebDriverUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

public class BasicTestsExecutor extends Assert {

    protected Logger LOG = LoggerFactory.getLogger(BasicTestsExecutor.class);

    protected WebDriver webDriver;
    private String testName;
    private long testStartTime;
    private static TestLinkUtils testLinkUtils;
    private int testCaseId;
    private int testCaseExternalId;

    public String getTestName() {
        return testName;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public BasicTestsExecutor() {
    }

    public BasicTestsExecutor(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    static {
        if (AppConfig.getProcessingResult()) {
            testLinkUtils = new TestLinkUtils(AppConfig.getBuildName());
        }
    }

    @BeforeMethod
    public void doBeforeMethod(Method method) {
        this.webDriver = WebDriverUtils.createWebDriverChrome();
        testName = method.getName();
        testStartTime = System.currentTimeMillis() / 1000;
        Thread.currentThread().setName(testName);

        LOG.info("\nTest method: " + testName + " from Class :" + this.getClass().getSimpleName() + " is start execution!\n");

        if (AppConfig.getProcessingResult()) {
            setTestLinkCaseIds(method);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void doAfterMethodBasic(Method method, ITestResult result) throws Exception {
        long testEndTime = System.currentTimeMillis() / 1000;

        if (result.isSuccess()) {
            LOG.info("\nTest method: " + method.getName() + " from Class :" + this.getClass().getSimpleName() + " is passed. Test execution time " + (testEndTime - testStartTime) + " sec.");
        } else {
            LOG.info("\nTest method: " + method.getName() + " from Class :" + this.getClass().getSimpleName() + " is failed. Test execution time " + (testEndTime - testStartTime) + " sec.");
        }

        if (AppConfig.getProcessingResult()) {
            addResultsToTestLink(method, result);
        }

        addScreenShotToReport(result);
        addWarningBlock(result);
        addLogBlock(result);
    }

    @AfterClass(alwaysRun = true)
    public void haltSessions() {
        closeBrowserSession();
    }

    public void closeBrowserSession() {
        if (webDriver != null) {
            if (((RemoteWebDriver) webDriver).getSessionId() != null) {
                try {
                    webDriver.close();
                } catch (NoSuchSessionException nsse) {
                    LOG.info("Can't close chrome driver due to: " + nsse.getMessage());
                }
            }
            webDriver.quit();
        }
    }

    private void addResultsToTestLink(Method method, ITestResult result) {
        if ((testCaseId != 0) || (testCaseExternalId != 0)) {
            String notes = testLinkUtils.generateNotesMessageForTestLink(method, result, 250);
            testLinkUtils.passResultsToTestLink(result, testCaseId, testCaseExternalId, notes);
            LOG.info(method.getName() + "-> Testlink. Test result is added to TestLink!");
        } else {
            LOG.info(method.getName() + "-> Testlink. Test result do not send to Testlink! Test Case id is not define.");
            LOG.info("Internal Id: " + testCaseId);
            LOG.info("External Id: " + testCaseExternalId);
        }
    }

    private int[] getUniqueIdsByTestCaseName(String tcValue) {
        int[] result = new int[2];
        boolean found = false;
        try {
            List<String> csvExportList = FileUtils.readLines(new File("target/test-classes/test/testlinkSuite/AutomationTestsProcessing.txt"), "utf-8");
            LOG.info("Count of test cases: " + csvExportList.size());
            for (String value : csvExportList) {
                String[] params = value.split(",");
                String testCaseName = params[0];
                if (testCaseName.equals(tcValue)) {
                    LOG.info("Processing value: " + value);
                    result[0] = Integer.valueOf(params[1]);
                    result[1] = Integer.valueOf(params[2]);
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } catch (Exception e) {
            LOG.info("Can not get test case id: " + e.getMessage());
        }
        return result;
    }

    private void setTestLinkCaseIds(Method method) {
        int[] idList = getUniqueIdsByTestCaseName("AUT - " + testName);
        if (idList != null) {
            testCaseId = idList[0];
            testCaseExternalId = idList[1];
        } else {
            LOG.error(method.getName() + "-> Testlink. Test case id is undefined. Please check it existing in Testlink.");
        }
    }

    private String getScreenShotAndGenerateLinkForReport(ITestResult result) {
        String screenShotReportBlock = "";
        if (!result.isSuccess()) {
            String projectAbsolutePath = System.getProperty("user.dir");
            String screenShotTargetFolderPath = projectAbsolutePath + "/target/surefire-reports/html/screenshots/" + result.getName();
            getScreenShot(screenShotTargetFolderPath);
            try {
                FileUtils.copyDirectory(new File(screenShotTargetFolderPath), new File(projectAbsolutePath + "/target/test-classes/screenshots/" + result.getName()));
            } catch (IOException e) {
                LOG.error("Can not copy screenshots folder from target  to resources, due to error: " + e.getMessage());
            }
            String screenShotImagePath = "screenshots/" + result.getName() + "/screenshot.png";
            screenShotReportBlock = String.format("<div> <a href='%s'><img src='%s' hight='100' width='100'/></a></div>", screenShotImagePath, screenShotImagePath);
        }
        return screenShotReportBlock;
    }

    private String generateLogBlockForReport(ITestResult result) {
        String link = "<a href=\"javascript:toggleElement('" + result.getName() + "-log', 'block')\" title=\"Click to expand/collapse\">" +
                "<b style=\"color:green\">Test LOG</b>" +
                "</a>";
        String block = "<div id = \"" + result.getName() + "-log\" style = \"display: none;\">" + CustomWriterAppender.getBufferContents(result.getName()) + "</div>";
        return link + block;
    }

    private String generateWarningBlock() {
        String fileName = "warning.png";
        File file = new File("");
        try {
            FileUtils.copyFileToDirectory(new File(file.getAbsolutePath() + "/target/test-classes/" + fileName), new File(file.getAbsolutePath() + "/target/surefire-reports/html/"));
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
        String path = "warning.png";
        return String.format("<div> <a href='%s'><img src='%s' hight='100' width='100'/></a></div>", path, path);
    }

    public void addScreenShotToReport(ITestResult result) {
        Reporter.setCurrentTestResult(result);
        addScreenshotToReport(result);
    }

    private void addWarningBlock(ITestResult result) {
        long executionTime = (result.getEndMillis() - result.getStartMillis()) / 1000;
        if (executionTime > 450) {
            Reporter.log(generateWarningBlock());
            LOG.info("WARNING. EXECUTION TIME LIMIT: " + result.getMethod().getMethodName());
        }
    }

    private void addLogBlock(ITestResult result) {
        Reporter.log(generateLogBlockForReport(result));
    }

    private void addScreenshotToReport(ITestResult result) {
        if (webDriver != null) {
            if (((RemoteWebDriver) webDriver).getSessionId() != null) {
                Reporter.log(getScreenShotAndGenerateLinkForReport(result));
                webDriver.getTitle();
            }
        }
    }
    
    private void getScreenShot(String pathToFile) {
        try {
            File failedTestCaseFolder = new File(pathToFile);
            failedTestCaseFolder.mkdirs();
            File scrFile;
            if (webDriver.getClass().getSimpleName().equals("RemoteWebDriver")) {
                Augmenter augmenter = new Augmenter();
                TakesScreenshot ts = (TakesScreenshot) augmenter.augment(webDriver);
                scrFile = ts.getScreenshotAs(OutputType.FILE);
            } else {
                scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            }
            FileUtils.copyFile(scrFile, new File(failedTestCaseFolder, "screenshot.png"));
            FileOutputStream fos = new FileOutputStream(new File(failedTestCaseFolder, "page_source.html"));
            fos.write(webDriver.getPageSource().getBytes());
            fos.close();

        } catch (Exception e) {
            LOG.error("An error occurred during screen shot taking: " + e.getMessage());
        }
    }

    protected boolean isChromeDriver(WebDriver webDriver) {
        Capabilities cap = ((RemoteWebDriver) webDriver).getCapabilities();
        String browsername = cap.getBrowserName();
        return "chrome".equalsIgnoreCase(browsername);
    }
    
}
