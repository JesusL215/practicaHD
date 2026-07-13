package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.model.domain.Ticket;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;

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

    private SmartParkApiClient apiClient;
    private List<ParkingSlot> slotsActuales;
    private AutoCompletionBinding autoCompletionBinding;

    @FXML
    public void initialize() {
        this.apiClient = new SmartParkApiClient();
        tipoVehiculoComboBox.getItems().addAll("AUTO", "MOTO");

        configurarFiltrosDeTexto(); // <--- 1. Configuramos las mayúsculas (Una sola vez)
        cargarDatosDesdeBackend();

        tipoVehiculoComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> filtrarSlotsDisponibles(newVal)
        );
    }

    private void cargarDatosDesdeBackend() {
        try {
            slotsActuales = apiClient.obtenerTodosLosSlots();
            actualizarMapaVisual();
            filtrarSlotsDisponibles(tipoVehiculoComboBox.getValue());

            renovarAutocompletado(); // <--- 2. ¡La magia en tiempo real!

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

            Long slotId = slotsActuales.stream()
                    .filter(s -> s.getNumero().equals(slotNum))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Slot no válido"))
                    .getId();

            apiClient.registrarEntrada(placa, tipo, slotId);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Entrada registrada en el servidor.");

            placaTextField.clear();
            tipoVehiculoComboBox.getSelectionModel().clearSelection();
            cargarDatosDesdeBackend();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de API", e.getMessage());
        }
    }

    @FXML
    private void handleRegistrarSalida() {
        String placa = placaSalidaTextField.getText().trim();
        boolean conLavado = lavadoCheckBox.isSelected();

        if (placa.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Alerta", "Por favor ingrese la placa del vehículo.");
            return;
        }

        try {
            Ticket ticketPagado = apiClient.registrarSalida(placa, conLavado);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Salida Registrada",
                    "¡Salida registrada exitosamente!\nTotal a pagar: S/ " + ticketPagado.getCostoTotal());

            preguntarYDescargarPDF(ticketPagado);

            placaSalidaTextField.clear();
            lavadoCheckBox.setSelected(false);
            cargarDatosDesdeBackend();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de API", e.getMessage());
        }
    }

    // --- MÉTODOS PARA EXPORTACIÓN PDF ---
    private void preguntarYDescargarPDF(Ticket ticket) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea generar y guardar el recibo en PDF?",
                ButtonType.YES, ButtonType.NO);

        confirmar.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                descargarYGuardarPDF(ticket);
            }
        });
    }

    private void descargarYGuardarPDF(Ticket ticket) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Recibo PDF");
        fileChooser.setInitialFileName("Recibo-" + ticket.getVehiculo().getPlaca() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));

        Stage stage = (Stage) placaSalidaTextField.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                byte[] pdfBytes = apiClient.descargarReciboPdf(ticket.getId());
                Files.write(file.toPath(), pdfBytes);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Recibo PDF guardado correctamente.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el PDF: " + e.getMessage());
            }
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
            Stage stage = (Stage) placaTextField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/smartpark/estacionamiento/view/Login.fxml"));
            stage.setTitle("SmartPark - Iniciar Sesión");
            stage.setScene(new Scene(root, 1000, 650));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cerrar sesión: " + e.getMessage());
        }
    }

    public void inicializarSesion(String rolUsuario) {
        if (!"ADMIN".equalsIgnoreCase(rolUsuario)) {
            btnAdministracion.setVisible(false);
            btnAdministracion.setManaged(false);
        }
    }

    @FXML
    private void handleVerAdministracion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/smartpark/estacionamiento/view/AdminSlots.fxml"));
            Parent adminView = loader.load();
            mainBorderPane.setCenter(adminView);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar el panel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerDashboard() {
        mainBorderPane.setCenter(vistaDashboard);
        cargarDatosDesdeBackend();
    }

    //Este método lo llamaremos UNA SOLA VEZ desde initialize()
    private void configurarFiltrosDeTexto() {
        // Forzamos mayúsculas automáticas en ambas cajas de texto
        placaTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.equals(newText.toUpperCase())) {
                placaTextField.setText(newText.toUpperCase());
            }
        });
        placaSalidaTextField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.equals(newText.toUpperCase())) {
                placaSalidaTextField.setText(newText.toUpperCase());
            }
        });
    }

    //Este método lo llamaremos TODO EL TIEMPO que cambie el estacionamiento
    private void renovarAutocompletado() {
        try {
            // Eliminamos la lista vieja de la memoria si existe
            if (autoCompletionBinding != null) {
                autoCompletionBinding.dispose();
            }

            // Traemos los datos frescos de Spring Boot
            List placasActivas = apiClient.obtenerPlacasActivas();

            // Creamos un nuevo autocompletado y guardamos su referencia
            autoCompletionBinding = TextFields.bindAutoCompletion(placaSalidaTextField, placasActivas);

        } catch (Exception e) {
            System.err.println("No se pudo renovar el autocompletado: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }
}