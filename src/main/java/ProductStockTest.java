package main.java;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductStock Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductStockTest {

    private ProductStock stock;

    @BeforeAll
    @Tag("lifecycle")
    void beforeAll() {
        System.out.println("starting test");
    }

    @AfterAll
    @Tag("lifecycle")
    void afterAll() {
        System.out.println("finished test");
    }

    @BeforeEach
    @Tag("lifecycle")
    void setUp() {
        stock = new ProductStock("product-1", "location-1", 100, 20, 200);
    }

    @AfterEach
    @Tag("lifecycle")
    void tearDown() {

    }

    // CONSTRUCTOR
    @Test
    @DisplayName("Valid constructor creates instance")
    @Tag("sanity")
    @Tag("constructor")
    void testConstructor() {
        ProductStock newStock = new ProductStock("product-1", "location", 50, 10, 100);
        assertNotNull(newStock);
        assertEquals("product-1", newStock.getProductId());
    }

    @Test
    @DisplayName("Constructor null productId throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorNullProductId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock(null, "location", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor blank productId throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorBlankProductId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("   ", "location", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor empty location throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorEmptyLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor initialOnHand > maxCapacity throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorOnHandExceedsCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", 150, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor negative initialOnHand throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorNegativeInitialOnHand() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", -5, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor negative reorderThreshold throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorNegativeReorderThreshold() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", 50, -10, 100);
        });
    }

    @Test
    @DisplayName("Constructor zero maxCapacity throws exception")
    @Tag("regression")
    @Tag("constructor")
    @Tag("validation")
    void testConstructorZeroMaxCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", 50, 10, 0);
        });
    }

    // GETTER TESTS
    @Test
    @DisplayName("All getters return correct values")
    @Tag("sanity")
    @Tag("getter")
    void getProductId() {

        assertAll("All getters should return correct values",
                () -> assertEquals("product-1", stock.getProductId()),
                () -> assertEquals("location-1", stock.getLocation()),
                () -> assertEquals(100, stock.getOnHand()),
                () -> assertEquals(0, stock.getReserved()),
                () -> assertEquals(100, stock.getAvailable()),
                () -> assertEquals(20, stock.getReorderThreshold()),
                () -> assertEquals(200, stock.getMaxCapacity())
        );
    }

    @Test
    @DisplayName("getLocation returns correct location")
    @Tag("sanity")
    @Tag("getter")
    void getLocation() {
        assertEquals("location-1", stock.getLocation());
    }

    @Test
    @DisplayName("getOnHand returns correct on-hand quantity")
    @Tag("sanity")
    @Tag("getter")
    void getOnHand() {
        assertEquals(100, stock.getOnHand());
    }

    @Test
    @DisplayName("getReserved returns correct reserved quantity")
    @Tag("sanity")
    @Tag("getter")
    void getReserved() {
        assertEquals(0, stock.getReserved());
    }

    @Test
    @DisplayName("getAvailable calculates correctly after reserve")
    @Tag("sanity")
    @Tag("getter")
    void getAvailable() {
        // onHand=100, reserved=30 , available=70
        stock.reserve(30);
        assertEquals(70, stock.getAvailable());
    }

    @Test
    @DisplayName("getReorderThreshold returns correct threshold")
    @Tag("sanity")
    @Tag("getter")
    void getReorderThreshold() {
        assertEquals(20, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("getMaxCapacity returns correct max capacity")
    @Tag("sanity")
    @Tag("getter")
    void getMaxCapacity() {
        assertEquals(200, stock.getMaxCapacity());
    }

    // LOCATION TESTS
    @Test
    @DisplayName("changeLocation updates location")
    @Tag("sanity")
    void changeLocation() {
        stock.changeLocation("new-loc");
        assertEquals("new-loc", stock.getLocation());
    }

    @Test
    @DisplayName("changeLocation with empty string throws exception")
    @Tag("regression")
    @Tag("validation")
    void testChangeLocationEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            stock.changeLocation("");
        });
    }

    // STOCK OPERATIONS - addStock
    @Test
    @DisplayName("addStock increases onHand")
    @Tag("sanity")
    @Tag("add")
    void addStock() {
        //  onHand=100, maxCapacity=200, amount=50 , onHand=150
        stock.addStock(50);
        assertEquals(150, stock.getOnHand());
    }

    @Test
    @DisplayName("addStock beyond capacity throws exception")
    @Tag("regression")
    @Tag("add")
    @Tag("capacity")
    void testAddStockBeyondCapacity() {
        //  onHand=100, maxCapacity=200, amount=150 , IllegalStateException thrown
        assertThrows(IllegalStateException.class, () -> {
            stock.addStock(150);
        });
    }

    @Test
    @DisplayName("addStock boundary: exactly to max capacity")
    @Tag("add")
    @Tag("capacity")
    void testAddStockBoundary() {
        //  onHand=100, maxCapacity=200, amount=100 , onHand=200 (exact capacity)
        stock.addStock(100);
        assertEquals(200, stock.getOnHand());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("addStock with invalid amounts throws exception")
    @Tag("regression")
    @Tag("add")
    @Tag("validation")
    @Tag("parameterized")
    void testAddStockInvalidAmounts(int amount) {
        //  amount={0,-1,-10} , IllegalArgumentException thrown
        assertThrows(IllegalArgumentException.class, () -> {
            stock.addStock(amount);
        });
    }

    // STOCK OPERATIONS - removeDamaged
    @Test
    @DisplayName("removeDamaged reduces onHand")
    @Tag("sanity")
    void removeDamaged() {
        //  onHand=100, amount=25 , onHand=75
        stock.removeDamaged(25);
        assertEquals(75, stock.getOnHand());
    }

    @Test
    @DisplayName("removeDamaged more than onHand throws exception")
    @Tag("regression")
    @Tag("validation")
    void testRemoveDamagedExceedsOnHand() {
        //  onHand=100, amount=150 , IllegalStateException thrown
        assertThrows(IllegalStateException.class, () -> {
            stock.removeDamaged(150);
        });
    }

    @Test
    @DisplayName("removeDamaged adjusts reserved if needed")
    @Tag("reservation")
    void testRemoveDamagedAdjustsReserved() {
        //  onHand=100, reserved=60, amount=50 , onHand=50, reserved=50 (adjusted)
        stock.reserve(60);
        stock.removeDamaged(50);
        assertEquals(50, stock.getOnHand());
        assertEquals(50, stock.getReserved());
    }

    @Test
    @DisplayName("removeDamaged with negative amount throws exception")
    @Tag("regression")
    @Tag("validation")
    void testRemoveDamagedNegative() {
        //  onHand=100, amount=-10 , IllegalArgumentException thrown
        assertThrows(IllegalArgumentException.class, () -> {
            stock.removeDamaged(-10);
        });
    }

    // RESERVATION
    @Test
    @DisplayName("reserve stock")
    @Tag("sanity")
    @Tag("reservation")
    void reserve() {
        // available=100, amount=30 , reserved=30, available=70
        stock.reserve(30);
        assertEquals(30, stock.getReserved());
        assertEquals(70, stock.getAvailable());
    }

    @Test
    @DisplayName("reserve more than available throws exception")
    @Tag("regression")
    @Tag("reservation")
    @Tag("validation")
    void testReserveInsufficient() {
        // available=100, amount=150 , IllegalStateException thrown
        assertThrows(IllegalStateException.class, () -> {
            stock.reserve(150);
        });
    }

    @Test
    @DisplayName("reserve with negative amount throws exception")
    @Tag("regression")
    @Tag("reservation")
    @Tag("validation")
    void testReserveNegative() {
        //  available=100, amount=-5
        assertThrows(IllegalArgumentException.class, () -> {
            stock.reserve(-5);
        });
    }

    // RELEASE RESERVATION
    @Test
    @DisplayName("releaseReservation")
    @Tag("sanity")
    @Tag("reservation")
    void releaseReservation() {
        // : reserved=50, amount=20 , reserved=30
        stock.reserve(50);
        stock.releaseReservation(20);
        assertEquals(30, stock.getReserved());
    }

    @Test
    @DisplayName("releaseReservation more than reserved throws exception")
    @Tag("regression")
    @Tag("reservation")
    @Tag("validation")
    void testReleaseReservationExceedsReserved() {
        // reserved=30, amount=31
        stock.reserve(30);
        assertThrows(IllegalStateException.class, () -> {
            stock.releaseReservation(31);
        });
    }

    @Test
    @DisplayName("releaseReservation with negative amount throws exception")
    @Tag("regression")
    @Tag("reservation")
    @Tag("validation")
    void testReleaseReservationNegative() {
        //  reserved=30, amount=-10
        stock.reserve(30);
        assertThrows(IllegalArgumentException.class, () -> {
            stock.releaseReservation(-10);
        });
    }

    // SHIPPING
    @Test
    @DisplayName("shipReserved reduces both onHand and reserved")
    @Tag("sanity")
    @Tag("shipping")
    void shipReserved() {
        //  onHand=100, reserved=40, amount=40 , onHand=60, reserved=0
        stock.reserve(40);
        stock.shipReserved(40);
        assertEquals(60, stock.getOnHand());
        assertEquals(0, stock.getReserved());
    }

    @Test
    @DisplayName("shipReserved more than reserved throws exception")
    @Tag("regression")
    @Tag("shipping")
    @Tag("validation")
    void testShipReservedExceedsReserved() {
        //  reserved=30, amount=31
        stock.reserve(30);
        assertThrows(IllegalStateException.class, () -> {
            stock.shipReserved(31);
        });
    }

    @Test
    @DisplayName("shipReserved with negative amount throws exception")
    @Tag("regression")
    @Tag("shipping")
    @Tag("validation")
    void testShipReservedNegative() {
        // reserved=30, amount=-5
        stock.reserve(30);
        assertThrows(IllegalArgumentException.class, () -> {
            stock.shipReserved(-5);
        });
    }

    // REORDER LOGIC
    @Test
    @DisplayName("isReorderNeeded when available below threshold")
    @Tag("sanity")
    @Tag("reorder")
    void isReorderNeeded() {
        //  available=100, threshold=20, reserve=85 , true: 15<20
        stock.reserve(85);
        assertTrue(stock.isReorderNeeded());
    }

    @Test
    @DisplayName("isReorderNeeded when available above threshold")
    @Tag("sanity")
    @Tag("reorder")
    void testIsReorderNeededFalse() {
        // available=100, threshold=20 , false: 100>20
        assertFalse(stock.isReorderNeeded());
    }

    @Test
    @DisplayName("isReorderNeeded boundary - available equals threshold")
    @Tag("reorder")
    void testIsReorderNeededBoundary() {
        //  available=100, threshold=20, reserve=80 ,  false: 20=20
        stock.reserve(80);
        assertFalse(stock.isReorderNeeded());
    }

    // THRESHOLD AND CAPACITY
    @Test
    @DisplayName("updateReorderThreshold")
    @Tag("sanity")
    @Tag("threshold")
    void updateReorderThreshold() {
        //  threshold=20, newThreshold=50 , threshold=50
        stock.updateReorderThreshold(50);
        assertEquals(50, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("updateReorderThreshold with negative value throws exception")
    @Tag("regression")
    @Tag("threshold")
    @Tag("validation")
    void testUpdateReorderThresholdNegative() {
        //  capacity=200, newThreshold=-5
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updateReorderThreshold(-5);
        });
    }

    @Test
    @DisplayName("updateReorderThreshold above capacity throws exception")
    @Tag("regression")
    @Tag("threshold")
    @Tag("validation")
    void testUpdateReorderThresholdAboveCapacity() {
        //  capacity=200, newThreshold=201
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updateReorderThreshold(201);
        });
    }

    // CAPACITY
    @Test
    @DisplayName("updateMaxCapacity")
    @Tag("sanity")
    @Tag("capacity")
    void updateMaxCapacity() {
        //  capacity=200, newCapacity=300 , capacity=300
        stock.updateMaxCapacity(300);
        assertEquals(300, stock.getMaxCapacity());
    }

    @Test
    @DisplayName("updateMaxCapacity less than onHand throws exception")
    @Tag("regression")
    @Tag("capacity")
    @Tag("validation")
    void testUpdateMaxCapacityTooSmall() {
        //  onHand=100, newCapacity=50
        assertThrows(IllegalStateException.class, () -> {
            stock.updateMaxCapacity(50);
        });
    }

    @Test
    @DisplayName("updateMaxCapacity adjusts threshold if needed")
    @Tag("capacity")
    @Tag("threshold")
    void testUpdateMaxCapacityAdjustsThreshold() {
        //  threshold=150, capacity=200, newCapacity=100 , capacity=100, threshold=100
        stock.updateReorderThreshold(150);
        stock.updateMaxCapacity(100);
        assertEquals(100, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("updateMaxCapacity with zero throws exception")
    @Tag("regression")
    @Tag("capacity")
    @Tag("validation")
    void testUpdateMaxCapacityZero() {
        // capacity=200, newCapacity=0
        assertThrows(IllegalArgumentException.class, () -> {
            stock.updateMaxCapacity(0);
        });
    }

    // STRING
    @Test
    @DisplayName("toString returns non-empty string")
    @Tag("sanity")
    @Tag("getter")
    void testToString() {
        String result = stock.toString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("product-1"));
    }

    // PERFORMANCE
    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS)
    @DisplayName("Performance test")
    @Tag("timing")
    void testPerformance() {
        stock.addStock(50);
        stock.reserve(30);
        stock.releaseReservation(10);
        stock.shipReserved(20);
        stock.removeDamaged(10);
    }

    // DISABLED FUTURE
    @Test
    @Disabled("Future feature: operations not implemented yet")
    @DisplayName("not implemented yet")
    @Tag("future")
    @Tag("disabled")
    void testDisabledOperations() {
        // test disabled
        fail("operations not implemented yet");
    }
}