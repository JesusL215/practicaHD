package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.domain.Ticket;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistorialController {

    @FXML private TextField txtBuscarPlaca;
    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFin;
    @FXML private ComboBox<String> comboEstado;

    @FXML private TableView<Ticket> tablaHistorial;
    @FXML private TableColumn<Ticket, String> colId;
    @FXML private TableColumn<Ticket, String> colPlaca;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colEntrada;
    @FXML private TableColumn<Ticket, String> colSalida;
    @FXML private TableColumn<Ticket, String> colEstado;
    @FXML private TableColumn<Ticket, Double> colMonto;

    private SmartParkApiClient apiClient;
    private ObservableList<Ticket> listaTicketsMaster = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        this.apiClient = new SmartParkApiClient();

        // Configurar opciones del ComboBox
        comboEstado.getItems().addAll("TODOS", "ACTIVO", "PAGADO", "ANULADO");
        comboEstado.setValue("TODOS");

        // Formateador de fechas para que se vea limpio
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Configurar cómo se lee cada columna
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));

        colPlaca.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVehiculo() != null) return new SimpleStringProperty(cellData.getValue().getVehiculo().getPlaca());
            return new SimpleStringProperty("N/A");
        });

        colTipo.setCellValueFactory(cellData -> {
            if (cellData.getValue().getParkingSlot() != null) {
                return new SimpleStringProperty(cellData.getValue().getParkingSlot().getTipoVehiculoPermitido());
            }
            return new SimpleStringProperty("N/A");
        });

        colEntrada.setCellValueFactory(cellData -> {
            if (cellData.getValue().getHoraEntrada() != null)
                return new SimpleStringProperty(cellData.getValue().getHoraEntrada().format(formatter));
            return new SimpleStringProperty("");
        });

        colSalida.setCellValueFactory(cellData -> {
            if (cellData.getValue().getHoraSalida() != null)
                return new SimpleStringProperty(cellData.getValue().getHoraSalida().format(formatter));
            return new SimpleStringProperty("-");
        });

        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado()));

        colMonto.setCellValueFactory(cellData -> {
            Double costo = cellData.getValue().getCostoTotal();
            return new SimpleObjectProperty<>(costo != null ? costo : 0.0);
        });

        cargarDatos();
    }

    private void cargarDatos() {
        try {
            // Obtenemos todos los tickets del servidor
            List<Ticket> tickets = apiClient.obtenerTodosLosTickets();
            listaTicketsMaster.setAll(tickets);
            tablaHistorial.setItems(listaTicketsMaster);
        } catch (Exception e) {
            mostrarAlerta("Error de Conexión", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    @FXML
    private void handleBuscar() {
        String placaBuscar = txtBuscarPlaca.getText().toUpperCase().trim();
        LocalDate inicio = dateInicio.getValue();
        LocalDate fin = dateFin.getValue();
        String estado = comboEstado.getValue();

        List<Ticket> filtrados = listaTicketsMaster.stream().filter(t -> {
            boolean coincidePlaca = placaBuscar.isEmpty() || (t.getVehiculo() != null && t.getVehiculo().getPlaca().contains(placaBuscar));
            boolean coincideEstado = "TODOS".equals(estado) || estado.equals(t.getEstado());

            // Ya no hay error aquí porque getHoraEntrada() ahora devuelve LocalDateTime real
            boolean coincideInicio = inicio == null || (t.getHoraEntrada() != null && !t.getHoraEntrada().toLocalDate().isBefore(inicio));
            boolean coincideFin = fin == null || (t.getHoraEntrada() != null && !t.getHoraEntrada().toLocalDate().isAfter(fin));

            return coincidePlaca && coincideEstado && coincideInicio && coincideFin;
        }).collect(Collectors.toList());

        tablaHistorial.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void handleLimpiar() {
        txtBuscarPlaca.clear();
        dateInicio.setValue(null);
        dateFin.setValue(null);
        comboEstado.setValue("TODOS");
        tablaHistorial.setItems(listaTicketsMaster);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }
}