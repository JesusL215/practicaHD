package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AdminSlotsController {

    // Componentes del Formulario
    @FXML private Label lblModo;
    @FXML private TextField txtNumero;
    @FXML private ComboBox<String> cbTipoVehiculo;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;

    // Componentes de la Tabla
    @FXML private TableView<ParkingSlot> tablaSlots;
    @FXML private TableColumn<ParkingSlot, Long> colId;
    @FXML private TableColumn<ParkingSlot, String> colNumero;
    @FXML private TableColumn<ParkingSlot, String> colTipo;
    @FXML private TableColumn<ParkingSlot, String> colEstado;

    private final SmartParkApiClient apiClient = new SmartParkApiClient();

    // Variable para saber si estamos editando o creando
    private ParkingSlot slotSeleccionado = null;

    @FXML
    public void initialize() {
        // 1. Llenamos las opciones de los ComboBox (Nuevos Estados incluidos)
        cbTipoVehiculo.getItems().addAll("AUTO", "MOTO");
        cbEstado.getItems().addAll("DISPONIBLE", "OCUPADO", "RESERVADO", "MANTENIMIENTO");

        // 2. Configuramos cómo las columnas leerán los datos del objeto ParkingSlot
        colId.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        colNumero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumero()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoVehiculoPermitido()));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));

        // 3. Agregamos un "Listener" para detectar cuando el usuario hace clic en una fila de la tabla
        tablaSlots.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarSlot(newSelection);
            }
        });

        // 4. Cargamos los datos iniciales desde el backend
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List<ParkingSlot> slots = apiClient.obtenerTodosLosSlots();
            tablaSlots.getItems().setAll(slots);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los espacios: " + e.getMessage());
        }
    }

    private void seleccionarSlot(ParkingSlot slot) {
        slotSeleccionado = slot;
        txtNumero.setText(slot.getNumero());
        cbTipoVehiculo.setValue(slot.getTipoVehiculoPermitido());
        cbEstado.setValue(slot.getEstado());

        lblModo.setText("Modo: Editando espacio ID " + slot.getId());
        btnGuardar.setText("Actualizar");
        btnEliminar.setDisable(false); // Habilitar botón de eliminar
    }

    @FXML
    private void handleLimpiar() {
        slotSeleccionado = null;
        txtNumero.clear();
        cbTipoVehiculo.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();

        lblModo.setText("Modo: Creando nuevo espacio");
        btnGuardar.setText("Guardar");
        btnEliminar.setDisable(true);
        tablaSlots.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleGuardar() {
        String numero = txtNumero.getText().trim();
        String tipo = cbTipoVehiculo.getValue();
        String estado = cbEstado.getValue();

        if (numero.isEmpty() || tipo == null || estado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Por favor complete todos los campos.");
            return;
        }

        ParkingSlot slot = new ParkingSlot();
        slot.setNumero(numero.toUpperCase()); // Estandarizamos en mayúsculas
        slot.setTipoVehiculoPermitido(tipo);
        slot.setEstado(estado);

        try {
            if (slotSeleccionado == null) {
                // Modo: CREAR
                apiClient.crearSlot(slot);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Espacio creado correctamente.");
            } else {
                // Modo: ACTUALIZAR
                apiClient.actualizarSlot(slotSeleccionado.getId(), slot);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Espacio actualizado correctamente.");
            }
            handleLimpiar();
            cargarDatos();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (slotSeleccionado == null) return;

        // Buena Práctica UX: Pedir confirmación antes de eliminar
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el espacio " + slotSeleccionado.getNumero() + "?",
                ButtonType.YES, ButtonType.NO);

        confirmar.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    apiClient.eliminarSlot(slotSeleccionado.getId());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Espacio eliminado permanentemente.");
                    handleLimpiar();
                    cargarDatos();
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "No se puede eliminar", e.getMessage());
                }
            }
        });
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