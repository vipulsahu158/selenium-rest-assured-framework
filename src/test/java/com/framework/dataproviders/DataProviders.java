package com.framework.dataproviders;

import com.framework.utils.ExcelUtils;
import org.testng.annotations.DataProvider;

/**
 * Each provider reads one sheet of TestData.xlsx. parallel = true runs the data rows concurrently
 * (pool size = data-provider-thread-count in testng.xml).
 */
public class DataProviders {

    @DataProvider(name = "loginData", parallel = true)
    public Object[][] loginData() {
        return ExcelUtils.getData("LoginData");
    }

    @DataProvider(name = "cartData", parallel = true)
    public Object[][] cartData() {
        return ExcelUtils.getData("CartData");
    }

    @DataProvider(name = "apiData", parallel = true)
    public Object[][] apiData() {
        return ExcelUtils.getData("ApiData");
    }
}
