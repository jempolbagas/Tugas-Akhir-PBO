package main.java.StockTradingApp.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class GUIUtils {

    public static Button createMenuButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setStyle(getButtonStyle(styleClass));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        return button;
    }

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

    public static TextField createStyledTextField() {
        TextField tf = new TextField();
        tf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return tf;
    }

    public static PasswordField createStyledPasswordField() {
        PasswordField pf = new PasswordField();
        pf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return pf;
    }

    public static Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px; -fx-font-weight: bold;");
        return label;
    }

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
