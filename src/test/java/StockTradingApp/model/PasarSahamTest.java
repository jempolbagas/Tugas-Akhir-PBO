package test.java.StockTradingApp.model;

import main.java.StockTradingApp.model.PasarSaham;
import main.java.StockTradingApp.model.Saham;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class PasarSahamTest {

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        // Create a PasarSaham instance. It initializes with some stocks.
        PasarSaham pasar = new PasarSaham();
        int threads = 20; // Increase thread count to increase contention
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicBoolean failed = new AtomicBoolean(false);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(threads);

        // Reader threads (simulate UI calling getAllSaham)
        // This iterates the map via new ArrayList(values())
        for (int i = 0; i < threads / 2; i++) {
            executor.submit(() -> {
                try {
                    while (running.get()) {
                        List<Saham> sahams = pasar.getAllSaham();
                        // Iterate over the list to ensure we actually read data
                        for (Saham s : sahams) {
                            assertNotNull(s.getKode());
                            // Just simulate some work
                            if (s.getHargaSekarang().doubleValue() < 0) {
                                throw new RuntimeException("Price cannot be negative");
                            }
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

        // Writer threads (simulate MarketService updates)
        // This iterates the map via values() and updates the Saham objects
        for (int i = 0; i < threads / 2; i++) {
            executor.submit(() -> {
                try {
                    while (running.get()) {
                        pasar.updateHargaSemua();
                        // Sleep a tiny bit to allow context switching
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

        assertFalse(failed.get(), "Exception occurred during concurrent access. Count: " + exceptionCount.get());
    }
}
