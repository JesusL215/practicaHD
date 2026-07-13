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

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final SmartParkApiClient apiClient = new SmartParkApiClient();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor ingrese su usuario y contraseña.");
            return;
        }

        try {
            // 1. Validamos credenciales en el backend
            Usuario usuarioLogueado = apiClient.login(username, password);

            // 2. Obtenemos la ventana (Stage) actual donde está el Login
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            // 3. Cargamos el diseño del Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartpark/estacionamiento/view/MainDashboard.fxml"));
            Parent root = loader.load();
            // Pasamos el rol al MainDashboardController
            MainDashboardController controller = loader.getController();
            controller.inicializarSesion(usuarioLogueado.getRol());

            // 4. Reemplazamos el contenido de la ventana actual SIN cerrarla
            stage.setTitle("SmartPark - Gestión Inteligente (" + usuarioLogueado.getRol() + ")");
            stage.setScene(new Scene(root, 1000, 650));

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado", e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}