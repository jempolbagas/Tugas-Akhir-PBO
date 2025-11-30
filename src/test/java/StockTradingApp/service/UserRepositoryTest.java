package test.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.service.DataManager;
import main.java.StockTradingApp.service.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserRepositoryTest {

    private DataManager mockDataManager;
    private UserRepository userRepository;
    private HashMap<String, Akun> testData;

    @BeforeEach
    public void setUp() throws Exception {
        mockDataManager = mock(DataManager.class);
        testData = new HashMap<>();

        // Setup initial data
        Akun akun = new Akun("testuser", "password", "Test User", "test@example.com", new BigDecimal("10000.00"));
        testData.put("testuser", akun);

        when(mockDataManager.loadData()).thenReturn(testData);

        userRepository = new UserRepository(mockDataManager);
    }

    @Test
    public void testLoadData_Success() {
        assertTrue(userRepository.existsByUsername("testuser"));
        Optional<Akun> akun = userRepository.findByUsername("testuser");
        assertTrue(akun.isPresent());
        assertEquals("testuser", akun.get().getUsername());
    }

    @Test
    public void testCreateAccount() throws DatabaseSaveException, IOException {
        Akun newAccount = new Akun("newuser", "password", "New User", "new@example.com", new BigDecimal("5000.00"));

        userRepository.createAccount(newAccount);

        assertTrue(userRepository.existsByUsername("newuser"));
        verify(mockDataManager, times(1)).saveData(any());
    }

    @Test
    public void testSaveData_DelegatesToDataManager() throws DatabaseSaveException, IOException {
        userRepository.saveData();
        verify(mockDataManager, times(1)).saveData(any());
    }

    @Test
    public void testFileNotFound_CreatesNewData() throws Exception {
        // Reset mock
        mockDataManager = mock(DataManager.class);
        when(mockDataManager.loadData()).thenThrow(new java.io.FileNotFoundException());

        userRepository = new UserRepository(mockDataManager);

        assertFalse(userRepository.existsByUsername("testuser")); // Empty
        List<String> notes = userRepository.getNotifications();
        assertEquals(1, notes.size());
        assertTrue(notes.get(0).contains("File data tidak ditemukan"));
        verify(mockDataManager, times(1)).saveData(any()); // Should save empty map
    }
}
