package tests.ui;

import com.qa.config.AppConfig;
import com.qa.tests.BasicTestsExecutor;
import com.qa.utils.SysUtils;
import org.testng.annotations.Test;

public class Demo extends BasicTestsExecutor {

    @Test
    public void testThatGooglePageCanBeLoaded(){
        webDriver.get( AppConfig.getStartUrl());
        SysUtils.sleep(6000);
    }
}
