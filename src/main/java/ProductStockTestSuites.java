package main.java;

import org.junit.platform.suite.api.*;

@Suite
@SelectClasses({
        ProductStockTestSuites.AllActiveTestsSuite.class,
        ProductStockTestSuites.BasicTestSuite.class,
        ProductStockTestSuites.RegressionSuite.class,
        ProductStockTestSuites.StockOperationsSuite.class,
        ProductStockTestSuites.CapacityThresholdSuite.class,
        ProductStockTestSuites.DisabledTestsSuite.class
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


    @Suite
    @SelectClasses(ProductStockTest.class)
    @IncludeTags({"regression", "validation"})
    @SuiteDisplayName("regression and validation suite")
    public static class RegressionSuite {
    }


    @Suite
    @SelectClasses(ProductStockTest.class)
    @IncludeTags({"add", "remove", "reservation", "shipping"})
    @SuiteDisplayName("Stock Operations Suite")
    public static class StockOperationsSuite {
    }


    @Suite
    @SelectClasses(ProductStockTest.class)
    @IncludeTags({"capacity", "threshold"})
    @SuiteDisplayName("Capacity and Threshold Suite")
    public static class CapacityThresholdSuite {
    }


    @Suite
    @SelectClasses(ProductStockTest.class)
    @IncludeTags({"future", "disabled"})
    @SuiteDisplayName("Disabled and Future Tests Suite")
    public static class DisabledTestsSuite {
    }
}