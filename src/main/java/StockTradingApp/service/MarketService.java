package main.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;
import main.java.StockTradingApp.model.PasarSaham;
import main.java.StockTradingApp.model.Saham;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MarketService {
    private final PasarSaham pasar;
    private Thread updateThread;
    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public MarketService() {
        this.pasar = new PasarSaham();
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void removeListener(Runnable listener) {
        listeners.remove(listener);
    }

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

    public void stopMarketUpdates() {
        if (updateThread != null) {
            updateThread.interrupt();
        }
    }

    public PasarSaham getPasarSaham() {
        return pasar;
    }

    // Delegation methods for convenience
    public boolean isPasarBuka() {
        return pasar.isPasarBuka();
    }

    public Saham getSaham(String kode) throws SahamTidakDitemukanException {
        return pasar.getSaham(kode);
    }

    public java.util.ArrayList<Saham> getAllSaham() {
        return pasar.getAllSaham();
    }
}
