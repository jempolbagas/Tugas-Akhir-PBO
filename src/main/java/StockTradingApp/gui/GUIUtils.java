package main.java.StockTradingApp.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Utility class for creating and styling JavaFX GUI components.
 * Provides helper methods for consistent "Cyberpunk/Dark Mode" styling.
 */
public class GUIUtils {

    /**
     * Creates a styled button for the menu or application.
     *
     * @param text       The text to display on the button.
     * @param styleClass The style category (e.g., "primary", "danger", "success", "tertiary").
     * @return A styled Button instance.
     */
    public static Button createMenuButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setStyle(getButtonStyle(styleClass));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        return button;
    }

    /**
     * Retrieves the CSS string for a specific button style.
     *
     * @param styleClass The style category.
     * @return The CSS string defining background, text color, etc.
     */
    public static String getButtonStyle(String styleClass) {
        switch (styleClass) {
            case "primary":
                return "-fx-background-color: linear-gradient(to right, #00ff88, #00ccff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "secondary":
                return "-fx-background-color: linear-gradient(to right, #6666ff, #8888ff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "tertiary":
                return "-fx-background-color: transparent; -fx-border-color: #444477; -fx-text-fill: #8888ff; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "danger":
                return "-fx-background-color: linear-gradient(to right, #ff4444, #ff6666); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "success":
                return "-fx-background-color: linear-gradient(to right, #28a745, #218838); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            default:
                return "-fx-background-color: #444477; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        }
    }

    /**
     * Creates a styled TextField with custom colors and padding.
     *
     * @return A styled TextField instance.
     */
    public static TextField createStyledTextField() {
        TextField tf = new TextField();
        tf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return tf;
    }

    /**
     * Creates a styled PasswordField with custom colors and padding.
     *
     * @return A styled PasswordField instance.
     */
    public static PasswordField createStyledPasswordField() {
        PasswordField pf = new PasswordField();
        pf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return pf;
    }

    /**
     * Creates a styled Label intended for form headers.
     *
     * @param text The text for the label.
     * @return A styled Label instance.
     */
    public static Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px; -fx-font-weight: bold;");
        return label;
    }

    /**
     * Displays an alert dialog with the application theme.
     *
     * @param title   The title of the alert.
     * @param message The content message.
     * @param type    The type of alert (e.g., INFORMATION, ERROR).
     */
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white;");

        alert.showAndWait();
    }
}
