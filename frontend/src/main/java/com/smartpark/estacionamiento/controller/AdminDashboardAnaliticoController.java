package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.dto.MovimientoDTO;
import com.smartpark.estacionamiento.model.dto.ReporteDashboardDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Map;

public class AdminDashboardAnaliticoController {

    @FXML private Label lblIngresosHoy;
    @FXML private Label lblIngresadosHoy;
    @FXML private Label lblEstacionados;
    @FXML private Label lblLibres;

    // IMPORTANTE: Tipos genéricos  en los gráficos
    @FXML private BarChart ingresosBarChart;
    @FXML private PieChart tipoVehiculoPieChart;
    @FXML private BarChart horasPicoBarChart;

    // IMPORTANTE: Tipos genéricos  en la tabla
    @FXML private TableView movimientosTable;
    @FXML private TableColumn colFecha;
    @FXML private TableColumn colHora;
    @FXML private TableColumn colPlaca;
    @FXML private TableColumn colTipo;
    @FXML private TableColumn colEstado;
    @FXML private TableColumn colMonto;

    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private ComboBox tipoVehiculoCombo;

    private SmartParkApiClient apiClient;

    @FXML
    public void initialize() {
        this.apiClient = new SmartParkApiClient();

        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoVehiculo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoPagado"));

        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        try {
            ReporteDashboardDTO reporte = apiClient.obtenerDatosDashboard();

            lblIngresosHoy.setText(String.format("S/ %.2f", reporte.getIngresosHoy()));
            lblIngresadosHoy.setText(String.valueOf(reporte.getVehiculosIngresadosHoy()));
            lblEstacionados.setText(String.valueOf(reporte.getVehiculosEstacionados()));
            lblLibres.setText(String.valueOf(reporte.getEspaciosLibres()));

            ingresosBarChart.getData().clear();
            XYChart.Series seriesIngresos = new XYChart.Series<>();
            if (reporte.getIngresosPorDia() != null) {
                // Tipado correcto en el bucle
                for (Map.Entry entry : reporte.getIngresosPorDia().entrySet()) {
                    seriesIngresos.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            ingresosBarChart.getData().add(seriesIngresos);

            tipoVehiculoPieChart.getData().clear();
            if (reporte.getIngresosPorTipoVehiculo() != null) {
                // Tipado correcto en el bucle
                for (Map.Entry entry : reporte.getIngresosPorTipoVehiculo().entrySet()) {
                    String etiqueta = String.format("%s (S/ %.2f)", entry.getKey(), entry.getValue());
                    tipoVehiculoPieChart.getData().add(new PieChart.Data(etiqueta, entry.getValue()));
                }
            }

            horasPicoBarChart.getData().clear();
            XYChart.Series seriesHoras = new XYChart.Series<>();
            if (reporte.getHorasPico() != null) {
                // Tipado correcto en el bucle (String, Integer)
                for (Map.Entry entry : reporte.getHorasPico().entrySet()) {
                    seriesHoras.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            horasPicoBarChart.getData().add(seriesHoras);

            movimientosTable.getItems().clear();
            if (reporte.getMovimientosRecientes() != null) {
                movimientosTable.getItems().addAll(reporte.getMovimientosRecientes());
            }

        } catch (Exception e) {
            mostrarAlerta("Error de Conexión", "No se pudo cargar el dashboard analítico: " + e.getMessage());
        }
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