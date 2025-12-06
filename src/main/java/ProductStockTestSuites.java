package main.java;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({
        ProductStockTestSuites.AllActiveTestsSuite.class,
        ProductStockTestSuites.BasicTestSuite.class,
})
@SuiteDisplayName("ProductStock Test Suites")
public class ProductStockTestSuites {

    @Suite
    @SelectClasses(ProductStockTest.class)
    @ExcludeTags({"future", "disabled"})
    @SuiteDisplayName("All Active Tests")
    public static class AllActiveTestsSuite {
    }


    @Suite
    @SelectClasses(ProductStockTest.class)
    @IncludeTags({"sanity", "constructor", "getter"})
    @SuiteDisplayName("Basic Test Suite")
    public static class BasicTestSuite {
    }
}