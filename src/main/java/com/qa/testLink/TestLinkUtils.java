package com.qa.testLink;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import com.qa.config.AppConfig;
import com.qa.utils.CustomWriterAppender;
import com.qa.utils.SysUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestLinkUtils {

    private static TestLinkAPI apiTestLink;
    private static int testPlanId;
    private static int buildId;
    private static String buildName;

    private static Map<String, String> platformMap = new HashMap<>();

    protected static Logger LOG = LoggerFactory.getLogger(TestLinkUtils.class);

    public TestLinkUtils(String buildName) {
        TestLinkUtils.buildName = buildName;
        createTLApi();
    }

    private void createTLApi() {
        URL testlinkURL = null;
        try {
            assert AppConfig.getTestLinkUrl() != null;
            testlinkURL = new URL(AppConfig.getTestLinkUrl());
        } catch (MalformedURLException e) {
            LOG.error("-> " + e);
        }
        apiTestLink = new TestLinkAPI(testlinkURL, AppConfig.getDevKey());
        TestPlan testPlan = apiTestLink.getTestPlanByName(AppConfig.getPlanName(), AppConfig.getProjectName());
        testPlanId = testPlan.getId();
        apiTestLink.createBuild(testPlanId, buildName, "Auto generated. " + AppConfig.getStartUrl());
        Build[] build = apiTestLink.getBuildsForTestPlan(testPlanId);
        for (Build buildCurrent : build) {
            String buildCurrentName = buildCurrent.getName();
            if (buildCurrentName.equals(buildName)) {
                buildId = buildCurrent.getId();
            }
        }

        platformMap.put("System environment: ", SysUtils.getOperatingSystem());
    }

    private ExecutionStatus getExecutionStatus(ITestResult result) {
        char execStatus = 'n';
        if (result.isSuccess()) {
            execStatus = 'p';
        }
        if (!result.isSuccess()) {
            execStatus = 'f';
        }
        return ExecutionStatus.getExecutionStatus(execStatus);
    }

    public void passResultsToTestLink(ITestResult result, int internalTestId, int externalTestId, String notes) {
        try {
            apiTestLink.setTestCaseExecutionResult(internalTestId, externalTestId, testPlanId, getExecutionStatus(result), buildId, buildName, notes, true, null, null, null, platformMap, true);
        } catch (TestLinkAPIException ex) {
            LOG.error("Can not pass results to TestLink due to :" + ex.getMessage());
        }
    }

    public String generateNotesMessageForTestLink(Method method, ITestResult result, int errorMessageLength) {
        String separator = "----------------------------------\n\n";
        String testCaseNotes;
        if (result.isSuccess()) {
            testCaseNotes = separator + "Test passed successfully.\n";
        } else {
            testCaseNotes = getNotesForFailedTestCase(method, result, errorMessageLength, separator);
        }
        return testCaseNotes;
    }

    private String getNotesForFailedTestCase(Method method, ITestResult result, int errorMessageLength, String separator) {
        String testCaseNotes;
        if (result.getThrowable() != null) {
            testCaseNotes = generateNotesForFailedTests(method, result, errorMessageLength, separator);
        } else {
            String errorMsg = "Can't get exception. Possible reason test skipped on before or after method.\n";
            System.out.println(errorMsg);
            testCaseNotes = separator + errorMsg;
        }
        return testCaseNotes;
    }

    private String generateNotesForFailedTests(Method method, ITestResult result, int errorMessageLength, String separator) {
        String exception = result.getThrowable().toString();
        if ((exception != null) && (exception.length() > errorMessageLength)) {
            exception = exception.substring(0, errorMessageLength) + "...";
        }
        String testLog = CustomWriterAppender.getBufferContents(method.getName()).replace("</br>", "");
        String resolution = separator + "Test failed due to error:\n" + exception + "\n" + separator + testLog;
        String pathToScreenShot = separator + "Link to screenshot :\n" + getScreenShotForTestLink(result) + "\n" + separator;
        System.out.println("Path to screenshot: " + pathToScreenShot);
        return resolution + pathToScreenShot;
    }

    private String getScreenShotForTestLink(ITestResult result) {
        return AppConfig.getTeamCityHost() + "/repository/download/" + AppConfig.getTeamcityBuildTypeId() + "/" + AppConfig.getTeamcityBuildId() + ":id/second_rerun.zip!/surefire-rerun/html/screenshots/failed/" + result.getName() + "/" + "screenshot.png";
    }

}
