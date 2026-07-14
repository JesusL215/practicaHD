package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final SmartParkApiClient apiClient = new SmartParkApiClient();

    @FXML
    private void handleLogin() {

        lblError.setText("");

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor ingrese su usuario y contraseña.");
            return;
        }

        try {
            Usuario usuarioLogueado = apiClient.login(username, password);

            Stage stage = (Stage) txtUsername.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/smartpark/estacionamiento/view/MainDashboard.fxml"));

            Parent root = loader.load();

            MainDashboardController controller = loader.getController();
            controller.inicializarSesion(usuarioLogueado.getRol());

            stage.setTitle("SmartPark - Gestión Inteligente (" + usuarioLogueado.getRol() + ")");
            stage.setScene(new Scene(root, 1000, 650));

        } catch (Exception e) {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }
}