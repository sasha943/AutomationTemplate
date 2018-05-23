package com.qa.config;

import com.qa.utils.AppUtil;

import java.util.Properties;

public abstract class AppConfig {

    private static final String PROPERTY_FILE_NAME = "config.properties";
    private static final Properties properties = AppUtil.loadPropertiesFromClassPath(PROPERTY_FILE_NAME);

    public static String getPlanName() {
        return properties.getProperty("TEST_PLAN_NAME");
    }

    public static String getStartUrl() {
        return properties.getProperty("START_URL");
    }

    public static String getTestLinkUrl() {
        return properties.getProperty("TEST_LINK_SERVER_URL");
    }

    public static String getProjectName() {
        return properties.getProperty("PROJECT_NAME");
    }

    public static String getDevKey() {
        return properties.getProperty("DEV_KEY");
    }

    public static String getTeamcityBuildTypeId() {
        return properties.getProperty("TEAM_CITY_BUILD_TYPE");
    }

    public static String getTeamcityBuildId() {
        return properties.getProperty("TEAM_CITY_BUILD_ID");
    }

    public static String getTeamCityHost() {
        return properties.getProperty("TEAM_CITY_BUILD_URL");
    }

    public static boolean getProcessingResult() {
        return Boolean.valueOf(properties.getProperty("PROCESSING_RESULT"));
    }

    public static String getBuildName() {
        return properties.getProperty("BUILD_NAME");
    }
}
