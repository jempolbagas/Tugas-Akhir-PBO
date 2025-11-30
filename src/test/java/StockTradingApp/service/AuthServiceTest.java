package test.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.AkunTidakDitemukanException;
import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.exception.DuplicateUsernameException;
import main.java.StockTradingApp.exception.InvalidPasswordException;
import main.java.StockTradingApp.exception.InvalidUsernameException;
import main.java.StockTradingApp.exception.PasswordSalahException;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.service.AuthService;
import main.java.StockTradingApp.service.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserRepository mockUserRepository;
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        mockUserRepository = mock(UserRepository.class);
        authService = new AuthService(mockUserRepository);
    }

    // ========== createAccount validation tests ==========

    @Test
    public void testCreateAccount_Success() throws Exception {
        when(mockUserRepository.existsByUsername("validuser")).thenReturn(false);

        authService.createAccount("validuser", "password123", "Valid User", "valid@example.com", new BigDecimal("10000.00"));

        verify(mockUserRepository, times(1)).createAccount(any(Akun.class));
    }

    @Test
    public void testCreateAccount_UsernameTooShort() {
        assertThrows(InvalidUsernameException.class, () -> {
            authService.createAccount("abc", "password123", "Short User", "short@example.com", new BigDecimal("10000.00"));
        });
    }

    @Test
    public void testCreateAccount_UsernameMinLength() throws Exception {
        when(mockUserRepository.existsByUsername("abcd")).thenReturn(false);

        // Username with exactly 4 characters should be valid
        authService.createAccount("abcd", "password123", "Min User", "min@example.com", new BigDecimal("10000.00"));

        verify(mockUserRepository, times(1)).createAccount(any(Akun.class));
    }

    @Test
    public void testCreateAccount_PasswordTooShort() {
        assertThrows(InvalidPasswordException.class, () -> {
            authService.createAccount("validuser", "12345", "Valid User", "valid@example.com", new BigDecimal("10000.00"));
        });
    }

    @Test
    public void testCreateAccount_PasswordMinLength() throws Exception {
        when(mockUserRepository.existsByUsername("validuser")).thenReturn(false);

        // Password with exactly 6 characters should be valid
        authService.createAccount("validuser", "123456", "Valid User", "valid@example.com", new BigDecimal("10000.00"));

        verify(mockUserRepository, times(1)).createAccount(any(Akun.class));
    }

    @Test
    public void testCreateAccount_DuplicateUsername() {
        when(mockUserRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> {
            authService.createAccount("existing", "password123", "Existing User", "existing@example.com", new BigDecimal("10000.00"));
        });
    }

    // ========== login tests ==========

    @Test
    public void testLogin_Success() throws Exception {
        Akun testAccount = new Akun("testuser", "password123", "Test User", "test@example.com", new BigDecimal("10000.00"));
        when(mockUserRepository.findByUsername("testuser")).thenReturn(Optional.of(testAccount));

        Akun result = authService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testLogin_AccountNotFound() {
        when(mockUserRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(AkunTidakDitemukanException.class, () -> {
            authService.login("nonexistent", "password123");
        });
    }

    @Test
    public void testLogin_WrongPassword() {
        Akun testAccount = new Akun("testuser", "correctpassword", "Test User", "test@example.com", new BigDecimal("10000.00"));
        when(mockUserRepository.findByUsername("testuser")).thenReturn(Optional.of(testAccount));

        assertThrows(PasswordSalahException.class, () -> {
            authService.login("testuser", "wrongpassword");
        });
    }
}
