package test.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.model.TradeResult;
import main.java.StockTradingApp.service.MarketService;
import main.java.StockTradingApp.service.AuthService;
import main.java.StockTradingApp.service.TradingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;

public class TradingServiceTest {

    private MarketService mockMarketService;
    private AuthService mockAuth;
    private TradingService tradingService;
    private Akun testAkun;
    private Saham testSaham;

    @BeforeEach
    public void setUp() {
        mockMarketService = mock(MarketService.class);
        mockAuth = mock(AuthService.class);
        tradingService = new TradingService(mockMarketService, mockAuth);

        // Initial balance: 10,000.00
        testAkun = new Akun("testuser", "password", "Test User", "test@example.com", new BigDecimal("10000.00"));

        // Stock price: 500.00
        testSaham = new Saham("TEST", "Test Stock", "Testing", new BigDecimal("500.00"));

        when(mockMarketService.isPasarBuka()).thenReturn(true);
        try {
            when(mockMarketService.getSaham(eq("TEST"))).thenReturn(testSaham);
        } catch (SahamTidakDitemukanException e) {
            fail("Setup failed: " + e.getMessage());
        }
    }

    @Test
    public void testBuyStock_Success() {
        // Buy 10 shares at 500.00 each = 5,000.00
        TradeResult result = tradingService.buyStock(testAkun, "TEST", 10);

        assertTrue(result.isSuccess());
        assertEquals("Pembelian berhasil!", result.getMessage());
        // 10000 - 5000 = 5000
        assertEquals(0, new BigDecimal("5000.00").compareTo(testAkun.getSaldo()));
        assertEquals(1, testAkun.getPortfolio().size());
        assertEquals(10, testAkun.getPortfolio().get("TEST").getJumlah());
    }

    @Test
    public void testBuyStock_InsufficientBalance() {
        // Try to buy 30 shares (15,000.00) with only 10,000.00 balance
        TradeResult result = tradingService.buyStock(testAkun, "TEST", 30);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Saldo tidak cukup!"));
        // Balance should not change
        assertEquals(0, new BigDecimal("10000.00").compareTo(testAkun.getSaldo()));
        assertEquals(0, testAkun.getPortfolio().size());
    }

    @Test
    public void testSellStock_NotOwned() {
        TradeResult result = tradingService.sellStock(testAkun, "TEST", 5);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Anda tidak memiliki saham"));
        assertEquals(0, new BigDecimal("10000.00").compareTo(testAkun.getSaldo()));
    }

    @Test
    public void testPrecision_NoFloatingPointErrors() {
        // Use a price that would cause floating point errors with double
        Saham precisionSaham = new Saham("PRE", "Precision Stock", "Testing", new BigDecimal("0.1"));
        try {
            when(mockMarketService.getSaham(eq("PRE"))).thenReturn(precisionSaham);
        } catch (SahamTidakDitemukanException e) {
            fail("Setup failed for precision test");
        }

        Akun precisionAkun = new Akun("precuser", "pass", "Prec User", "p@e.com", new BigDecimal("1.0"));

        // Buy 3 shares at 0.1 each = 0.3
        tradingService.buyStock(precisionAkun, "PRE", 3);

        // Expected balance: 1.0 - 0.3 = 0.7
        assertEquals(0, new BigDecimal("0.70").compareTo(precisionAkun.getSaldo()));
    }
}
