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


    @Test
    @DisplayName("Valid constructor creates instance")
    @Tag("sanity")
    void testConstructor() {
        ProductStock newStock = new ProductStock("product-1", "location", 50, 10, 100);
        assertNotNull(newStock);
        assertEquals("product-1", newStock.getProductId());
    }

    @Test
    @DisplayName("Constructor null productId throws exception")
    @Tag("regression")
    void testConstructorNullProductId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock(null, "location", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor blank productId throws exception")
    @Tag("regression")
    void testConstructorBlankProductId() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("   ", "location", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor empty location throws exception")
    @Tag("regression")
    void testConstructorEmptyLocation() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "", 50, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor initialOnHand > maxCapacity throws exception")
    @Tag("regression")
    void testConstructorOnHandExceedsCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", 150, 10, 100);
        });
    }

    @Test
    @DisplayName("Constructor negative initialOnHand throws exception")
    @Tag("regression")
    void testConstructorNegativeInitialOnHand() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ProductStock("product-1", "location", -5, 10, 100);
        });
    }


    @Test
    @DisplayName("All getters return correct values")
    @Tag("sanity")
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
    void getLocation() {
        assertEquals("location-1", stock.getLocation());
    }

    @Test
    @DisplayName("getOnHand returns correct on-hand quantity")
    @Tag("sanity")
    void getOnHand() {
        assertEquals(100, stock.getOnHand());
    }

    @Test
    @DisplayName("getReserved returns correct reserved quantity")
    @Tag("sanity")
    void getReserved() {
        assertEquals(0, stock.getReserved());
    }

    @Test
    @DisplayName("getAvailable calculates correctly after reserve")
    @Tag("sanity")
    void getAvailable() {
        // onHand=100, reserved=30 , available=70
        stock.reserve(30);
        assertEquals(70, stock.getAvailable());
    }

    @Test
    @DisplayName("getReorderThreshold returns correct threshold")
    @Tag("sanity")
    void getReorderThreshold() {
        assertEquals(20, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("getMaxCapacity returns correct max capacity")
    @Tag("sanity")
    void getMaxCapacity() {
        assertEquals(200, stock.getMaxCapacity());
    }


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
    void testChangeLocationEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            stock.changeLocation("");
        });
    }


    @Test
    @DisplayName("addStock increases onHand")
    @Tag("sanity")
    void addStock() {
        //  onHand=100, maxCapacity=200, amount=50 , onHand=150
        stock.addStock(50);
        assertEquals(150, stock.getOnHand());
    }
    @Test
    @DisplayName("addStock beyond capacity throws exception")
    @Tag("regression")
    void testAddStockBeyondCapacity() {
        //  onHand=100, maxCapacity=200, amount=150
        assertThrows(IllegalStateException.class, () -> {
            stock.addStock(150);
        });
    }

    @Test
    @DisplayName("addStock to max capacity")
    @Tag("add")
    void testAddStockBoundary() {
        //  onHand=100, maxCapacity=200, amount=100 , onHand=200
        stock.addStock(100);
        assertEquals(200, stock.getOnHand());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("addStock with invalid amounts throws exception")
    @Tag("regression")
    void testAddStockInvalidAmounts(int amount) {
        //  amount={0,-1,-10}
        assertThrows(IllegalArgumentException.class, () -> {
            stock.addStock(amount);
        });
    }
    @Test
    @DisplayName("removeDamaged reduces onHand")
    @Tag("sanity")
    void removeDamaged() {
        //  onHand=100, amount=25 , onHand=75
        stock.removeDamaged(25);
        assertEquals(75, stock.getOnHand());
    }

    @Test
    @DisplayName("reserve stock")
    @Tag("sanity")
    void reserve() {
        // available=100, amount=30 , reserved=30, available=70
        stock.reserve(30);
        assertEquals(30, stock.getReserved());
        assertEquals(70, stock.getAvailable());
    }

    @Test
    @DisplayName("releaseReservation")
    @Tag("sanity")
    void releaseReservation() {
        // : reserved=50, amount=20 , reserved=30
        stock.reserve(50);
        stock.releaseReservation(20);
        assertEquals(30, stock.getReserved());
    }

    @Test
    @DisplayName("shipReserved reduces both onHand and reserved")
    @Tag("sanity")
    void shipReserved() {
        //  onHand=100, reserved=40, amount=40 , onHand=60, reserved=0
        stock.reserve(40);
        stock.shipReserved(40);
        assertEquals(60, stock.getOnHand());
        assertEquals(0, stock.getReserved());
    }


    @Test
    @DisplayName("isReorderNeeded when available below threshold")
    @Tag("sanity")
    void isReorderNeeded() {
        //  available=100, threshold=20, reserve=85 , true: 15<20
        stock.reserve(85);
        assertTrue(stock.isReorderNeeded());
    }


    @Test
    @DisplayName("updateReorderThreshold")
    @Tag("sanity")
    void updateReorderThreshold() {
        //  threshold=20, newThreshold=50 , threshold=50
        stock.updateReorderThreshold(50);
        assertEquals(50, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("updateMaxCapacity")
    @Tag("sanity")
    void updateMaxCapacity() {
        //  capacity=200, newCapacity=300 , capacity=300
        stock.updateMaxCapacity(300);
        assertEquals(300, stock.getMaxCapacity());
    }


    @Test
    @DisplayName("toString returns non-empty string")
    @Tag("sanity")
    void testToString() {
        String result = stock.toString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("product-1"));
    }
}