package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.Tarifa;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class AdminTarifasController {

    @FXML private Label lblServicio;
    @FXML private TextField txtMonto;
    @FXML private Button btnGuardar;
    @FXML private TableView<Tarifa> tablaTarifas;
    @FXML private TableColumn<Tarifa, String> colCodigo;
    @FXML private TableColumn<Tarifa, String> colDescripcion;
    @FXML private TableColumn<Tarifa, Number> colMonto;

    private final SmartParkApiClient apiClient = new SmartParkApiClient();
    private Tarifa tarifaSeleccionada = null;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory((TableColumn.CellDataFeatures<Tarifa, String> c) -> new SimpleStringProperty(c.getValue().getCodigo()));
        colDescripcion.setCellValueFactory((TableColumn.CellDataFeatures<Tarifa, String> c) -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colMonto.setCellValueFactory((TableColumn.CellDataFeatures<Tarifa, Number> c) -> new SimpleDoubleProperty(c.getValue().getMonto()));

        tablaTarifas.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) -> {
            if (nueva != null) {
                seleccionarTarifa(nueva);
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            List tarifas = apiClient.obtenerTodasLasTarifas();
            tablaTarifas.getItems().setAll(tarifas);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar las tarifas.");
        }
    }

    private void seleccionarTarifa(Tarifa tarifa) {
        tarifaSeleccionada = tarifa;
        lblServicio.setText("Modificando: " + tarifa.getDescripcion());
        txtMonto.setText(String.valueOf(tarifa.getMonto()));
        txtMonto.setDisable(false);
        btnGuardar.setDisable(false);
    }

    @FXML
    private void handleGuardar() {
        try {
            double nuevoMonto = Double.parseDouble(txtMonto.getText());
            tarifaSeleccionada.setMonto(nuevoMonto);

            apiClient.actualizarTarifa(tarifaSeleccionada.getId(), tarifaSeleccionada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Precio actualizado. Aplicará para las próximas salidas.");
            cargarDatos();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Inválido", "Ingrese un número válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
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