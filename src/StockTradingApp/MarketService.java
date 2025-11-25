package StockTradingApp;

public class MarketService {
    private final PasarSaham pasar;
    private Thread updateThread;

    public MarketService() {
        this.pasar = new PasarSaham();
    }

    public void startMarketUpdates() {
        if (updateThread != null && updateThread.isAlive()) {
            return;
        }

        updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000); // 10 seconds
                    if (pasar.isPasarBuka()) {
                        pasar.updateHargaSemua();
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
