package test.java.StockTradingApp.model;

import main.java.StockTradingApp.model.Saham;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class SahamHistoryTest {

    @Test
    public void testInitialHistoryState() {
        BigDecimal initialPrice = new BigDecimal("1000.00");
        Saham saham = new Saham("TEST", "Test Stock", "Technology", initialPrice);

        List<BigDecimal> priceHistory = saham.getPriceHistory();
        List<String> timeHistory = saham.getTimeHistory();

        // Initial state should have exactly one entry
        assertEquals(1, priceHistory.size(), "Price history should have initial entry");
        assertEquals(1, timeHistory.size(), "Time history should have initial entry");
        assertEquals(initialPrice.setScale(2), priceHistory.get(0).setScale(2), "Initial price should match");
    }

    @Test
    public void testHistoryGrowsWithUpdates() {
        Saham saham = new Saham("TEST", "Test Stock", "Technology", new BigDecimal("1000.00"));
        Random random = new Random(42); // Fixed seed for reproducibility

        // Initial history has 1 entry
        assertEquals(1, saham.getPriceHistory().size());

        // Perform 5 updates
        for (int i = 0; i < 5; i++) {
            saham.updateHarga(random);
        }

        List<BigDecimal> priceHistory = saham.getPriceHistory();
        List<String> timeHistory = saham.getTimeHistory();

        // Should now have 6 entries (1 initial + 5 updates)
        assertEquals(6, priceHistory.size(), "Price history should have 6 entries after 5 updates");
        assertEquals(6, timeHistory.size(), "Time history should have 6 entries after 5 updates");
    }

    @Test
    public void testHistoryLimitEnforcement() {
        Saham saham = new Saham("TEST", "Test Stock", "Technology", new BigDecimal("1000.00"));
        Random random = new Random(42);

        // Perform 25 updates (1 initial + 25 updates = 26 total, but limited to 20)
        for (int i = 0; i < 25; i++) {
            saham.updateHarga(random);
        }

        List<BigDecimal> priceHistory = saham.getPriceHistory();
        List<String> timeHistory = saham.getTimeHistory();

        // History should be capped at 20 entries
        assertEquals(20, priceHistory.size(), "Price history should be limited to 20 entries");
        assertEquals(20, timeHistory.size(), "Time history should be limited to 20 entries");
    }

    @Test
    public void testHistoryListsAreSameSize() {
        Saham saham = new Saham("TEST", "Test Stock", "Technology", new BigDecimal("1000.00"));
        Random random = new Random(42);

        // Perform various updates and check consistency
        for (int i = 0; i < 30; i++) {
            saham.updateHarga(random);
            List<BigDecimal> priceHistory = saham.getPriceHistory();
            List<String> timeHistory = saham.getTimeHistory();
            assertEquals(priceHistory.size(), timeHistory.size(),
                    "Price and time history should always have the same size");
        }
    }

    @Test
    public void testGetHistoryReturnsDefensiveCopy() {
        Saham saham = new Saham("TEST", "Test Stock", "Technology", new BigDecimal("1000.00"));

        List<BigDecimal> priceHistory1 = saham.getPriceHistory();
        List<String> timeHistory1 = saham.getTimeHistory();

        // Modify the returned lists
        priceHistory1.add(new BigDecimal("9999"));
        timeHistory1.add("99:99:99");

        // Get fresh copies
        List<BigDecimal> priceHistory2 = saham.getPriceHistory();
        List<String> timeHistory2 = saham.getTimeHistory();

        // Original data should be unaffected
        assertEquals(1, priceHistory2.size(), "Original price history should be unaffected");
        assertEquals(1, timeHistory2.size(), "Original time history should be unaffected");
    }

    @Test
    public void testConcurrentHistoryAccess() throws InterruptedException {
        Saham saham = new Saham("TEST", "Test Stock", "Technology", new BigDecimal("1000.00"));

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threads);

        // Reader threads - continuously read history
        for (int i = 0; i < threads / 2; i++) {
            executor.submit(() -> {
                try {
                    while (running.get()) {
                        List<BigDecimal> prices = saham.getPriceHistory();
                        List<String> times = saham.getTimeHistory();

                        // Verify sizes are equal and within bounds
                        assertTrue(prices.size() <= 20, "Price history should not exceed 20");
                        assertTrue(times.size() <= 20, "Time history should not exceed 20");

                        // Iterate over to ensure no ConcurrentModificationException
                        for (int j = 0; j < prices.size(); j++) {
                            assertNotNull(prices.get(j));
                        }
                        for (String time : times) {
                            assertNotNull(time);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.set(true);
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // Writer threads - continuously update price (which adds to history)
        // Use seeded Random for reproducibility
        for (int i = 0; i < threads / 2; i++) {
            final int seed = 42 + i;
            executor.submit(() -> {
                try {
                    Random localRandom = new Random(seed);
                    while (running.get()) {
                        saham.updateHarga(localRandom);
                        Thread.yield();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed.set(true);
                    exceptionCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        Thread.sleep(2000); // Run for 2 seconds
        running.set(false);
        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertFalse(failed.get(), "Exception occurred during concurrent history access. Count: " + exceptionCount.get());

        // Final check: history should be limited to 20
        assertTrue(saham.getPriceHistory().size() <= 20, "Final price history should not exceed 20");
        assertTrue(saham.getTimeHistory().size() <= 20, "Final time history should not exceed 20");
    }
}
