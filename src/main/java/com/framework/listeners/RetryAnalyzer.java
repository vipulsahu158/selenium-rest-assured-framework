package com.framework.listeners;

import com.framework.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test up to retry.count times (default 1). Public demo sites are shared and
 * occasionally slow or unreachable, so one retry separates a flaky network from a real defect.
 * A test that fails every attempt is still reported as failed. Set -Dretry.count=0 to turn it off.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        return attempts++ < ConfigReader.getInt("retry.count", 1);
    }
}
