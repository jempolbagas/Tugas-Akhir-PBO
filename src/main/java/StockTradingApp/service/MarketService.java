package main.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;
import main.java.StockTradingApp.model.PasarSaham;
import main.java.StockTradingApp.model.Saham;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service class for managing market updates.
 * Runs a background thread to simulate real-time price changes and notifies listeners.
 */
public class MarketService {
    private final PasarSaham pasar;
    private Thread updateThread;
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    /**
     * Constructs a new MarketService.
     * Initializes the stock market instance.
     */
    public MarketService() {
        this.pasar = new PasarSaham();
    }

    /**
     * Adds a listener to be notified of market updates.
     *
     * @param listener The Runnable to execute on update.
     */
    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Removes a market update listener.
     *
     * @param listener The Runnable to remove.
     */
    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

    /**
     * Starts the market update simulation thread.
     * Updates prices every 10 seconds if the market is open and triggers listeners.
     */
    public void startMarketUpdates() {
        synchronized (this) {
            if (updateThread != null && updateThread.isAlive()) {
                return;
            }

            updateThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(10000); // 10 seconds
                        if (pasar.isPasarBuka()) {
                            pasar.updateHargaSemua();
                            listeners.forEach(listener -> {
                                try {
                                    listener.run();
                                } catch (Exception e) {
                                    System.err.println("Error in market listener: " + e.getMessage());
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            updateThread.setDaemon(true);
            updateThread.start();
        }
    }

    /**
     * Stops the market update simulation thread.
     */
    public void stopMarketUpdates() {
        if (updateThread != null) {
            updateThread.interrupt();
        }
    }

    /**
     * Gets the underlying PasarSaham instance.
     *
     * @return The PasarSaham object.
     */
    public PasarSaham getPasarSaham() {
        return pasar;
    }

    // Delegation methods for convenience
    /**
     * Checks if the market is currently open.
     *
     * @return true if open, false otherwise.
     */
    public boolean isPasarBuka() {
        return pasar.isPasarBuka();
    }

    /**
     * Retrieves a stock by its code.
     *
     * @param kode The stock code.
     * @return The Saham object.
     * @throws SahamTidakDitemukanException if the stock is not found.
     */
    public Saham getSaham(String kode) throws SahamTidakDitemukanException {
        return pasar.getSaham(kode);
    }

    /**
     * Retrieves all available stocks.
     *
     * @return An ArrayList of all Saham objects.
     */
    public java.util.ArrayList<Saham> getAllSaham() {
        return pasar.getAllSaham();
    }
}
