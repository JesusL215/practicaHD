package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.model.domain.Ticket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class MainDashboardController {

    @FXML private TextField placaTextField, placaSalidaTextField;
    @FXML private ComboBox<String> tipoVehiculoComboBox, slotComboBox;
    @FXML private CheckBox lavadoCheckBox;
    @FXML private Label statusLabel;
    @FXML private GridPane parkingGrid;
    @FXML private BorderPane mainBorderPane;
    @FXML private HBox vistaDashboard;
    @FXML private Button btnAdministracion;

    // ¡NUEVO: Nuestro único puente de comunicación con el Backend!
    private SmartParkApiClient apiClient;

    // Guardamos los slots en memoria para acceder a sus IDs rápidamente
    private List<ParkingSlot> slotsActuales;

    @FXML
    public void initialize() {
        // 1. Inicializamos el cliente HTTP (¡Adiós a la base de datos local!)
        this.apiClient = new SmartParkApiClient();

        tipoVehiculoComboBox.getItems().addAll("AUTO", "MOTO"); // En mayúscula como en el backend

        // 2. Cargamos los datos iniciales
        cargarDatosDesdeBackend();

        tipoVehiculoComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> filtrarSlotsDisponibles(newVal)
        );
    }

    private void cargarDatosDesdeBackend() {
        try {
            // Hacemos una petición GET a http://localhost:8080/api/slots
            slotsActuales = apiClient.obtenerTodosLosSlots();
            actualizarMapaVisual();
            filtrarSlotsDisponibles(tipoVehiculoComboBox.getValue());
            statusLabel.setText("Conectado al servidor Spring Boot. Mapa actualizado.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    private void actualizarMapaVisual() {
        parkingGrid.getChildren().clear();

        int col = 0;
        int row = 0;

        for (ParkingSlot slot : slotsActuales) {
            Button btn = new Button(slot.getNumero() + "\n" + slot.getTipoVehiculoPermitido());
            btn.setPrefSize(80, 60);
            btn.getStyleClass().add("slot-button");

            // Comparamos con el estado que devuelve Spring Boot
            if ("DISPONIBLE".equals(slot.getEstado())) {
                btn.getStyleClass().add("slot-free");
            } else {
                btn.getStyleClass().add("slot-occupied");
            }

            btn.setOnAction(e -> {
                if ("DISPONIBLE".equals(slot.getEstado())) {
                    tipoVehiculoComboBox.setValue(slot.getTipoVehiculoPermitido());
                    slotComboBox.setValue(slot.getNumero());
                }
            });

            parkingGrid.add(btn, col, row);
            col++;
            if (col > 3) { col = 0; row++; }
        }
    }

    private void filtrarSlotsDisponibles(String tipo) {
        if (tipo == null || slotsActuales == null) {
            slotComboBox.getItems().clear();
            slotComboBox.setDisable(true);
            return;
        }

        List<String> slotsFiltrados = slotsActuales.stream()
                .filter(s -> s.getTipoVehiculoPermitido().equalsIgnoreCase(tipo) && "DISPONIBLE".equals(s.getEstado()))
                .map(ParkingSlot::getNumero)
                .collect(Collectors.toList());

        slotComboBox.getItems().setAll(slotsFiltrados);
        slotComboBox.setDisable(false);
    }

    @FXML
    private void handleRegistrarEntrada() {
        try {
            String placa = placaTextField.getText();
            String tipo = tipoVehiculoComboBox.getValue();
            String slotNum = slotComboBox.getValue();

            if(placa.isEmpty() || tipo == null || slotNum == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Complete todos los campos."); return;
            }

            // Buscamos el ID del slot seleccionado
            Long slotId = slotsActuales.stream()
                    .filter(s -> s.getNumero().equals(slotNum))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Slot no válido"))
                    .getId();

            // Petición POST al Backend
            apiClient.registrarEntrada(placa, tipo, slotId);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Entrada registrada en el servidor.");

            placaTextField.clear();
            tipoVehiculoComboBox.getSelectionModel().clearSelection();

            // Refrescamos el mapa haciendo otra petición GET
            cargarDatosDesdeBackend();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de API", e.getMessage());
        }
    }

    @FXML
    private void handleRegistrarSalida() {
        String placa = placaSalidaTextField.getText().trim();
        boolean conLavado = lavadoCheckBox.isSelected();

        if(placa.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Alerta", "Por favor ingrese la placa del vehículo.");
            return;
        }

        try {
            Ticket ticketPagado = apiClient.registrarSalida(placa, conLavado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Salida Registrada", "¡Salida registrada exitosamente!\nTotal a pagar: S/ " + ticketPagado.getCostoTotal());

            placaSalidaTextField.clear();
            lavadoCheckBox.setSelected(false);

            cargarDatosDesdeBackend();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de API", e.getMessage());
        }
    }

    @FXML
    private void handleDeshacer() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente", "Esta función será migrada a la nube en el Sprint 2.");
    }

    @FXML
    private void handleVerReporte() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente", "Los reportes se generarán en PDF desde el servidor en el Sprint 2.");
    }

    @FXML
    private void handleVerHistorial() {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Próximamente", "El historial se consumirá desde la base de datos en la nube en el Sprint 2.");
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            // Obtenemos la ventana actual
            Stage stage = (Stage) placaTextField.getScene().getWindow();

            // Cargamos la vista del Login
            Parent root = FXMLLoader.load(getClass().getResource("/com/smartpark/estacionamiento/view/Login.fxml"));

            // Volvemos a poner la escena del login en la misma ventana
            stage.setTitle("SmartPark - Iniciar Sesión");
            stage.setScene(new Scene(root, 1000, 650));

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cerrar sesión: " + e.getMessage());
        }
    }

    // 1. Oculta el botón si el usuario NO es ADMIN
    public void inicializarSesion(String rolUsuario) {
        if (!"ADMIN".equalsIgnoreCase(rolUsuario)) {
            btnAdministracion.setVisible(false);
            btnAdministracion.setManaged(false); // Evita que deje un hueco vacío en el menú
        }
    }

    // 2. Navegar a la vista de Administración
    @FXML
    private void handleVerAdministracion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartpark/estacionamiento/view/AdminSlots.fxml"));
            Parent adminView = loader.load();

            // Reemplazamos el centro con la nueva pantalla
            mainBorderPane.setCenter(adminView);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar el panel: " + e.getMessage());
            e.printStackTrace(); // Para ver en consola si falta algún import o hay error de FXML
        }
    }

    // 3. Regresar al mapa principal
    @FXML
    private void handleVerDashboard() {
        // Volvemos a colocar la vista original guardada en memoria
        mainBorderPane.setCenter(vistaDashboard);
        // Refrescamos los datos por si el Admin agregó un espacio nuevo
        cargarDatosDesdeBackend();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        // Usamos runLater por si la alerta se llama desde un hilo secundario (petición HTTP)
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }
}