package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.api.SmartParkApiClient;
import com.smartpark.estacionamiento.model.dto.ReporteDashboardDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;

import java.util.Map;

public class AdminDashboardAnaliticoController {

    @FXML private BarChart ingresosBarChart;
    @FXML private PieChart tipoVehiculoPieChart;

    private SmartParkApiClient apiClient;

    @FXML
    public void initialize() {
        this.apiClient = new SmartParkApiClient();
        cargarDatos();
    }

    @FXML
    private void cargarDatos() {
        try {
            // 1. Pedimos los datos procesados al Backend
            ReporteDashboardDTO reporte = apiClient.obtenerDatosDashboard();

            // 2. Llenar el Gráfico de Barras (Ingresos por Día)
            ingresosBarChart.getData().clear();
            XYChart.Series series = new XYChart.Series<>();

            if (reporte.getIngresosPorDia() != null) {
                for (Map.Entry entry : reporte.getIngresosPorDia().entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            ingresosBarChart.getData().add(series);

            // 3. Llenar el Gráfico de Pastel (Distribución por Vehículo)
            tipoVehiculoPieChart.getData().clear();
            if (reporte.getIngresosPorTipoVehiculo() != null) {
                for (Map.Entry entry : reporte.getIngresosPorTipoVehiculo().entrySet()) {
                    String etiqueta = String.format("%s (S/ %.2f)", entry.getKey(), entry.getValue());
                    tipoVehiculoPieChart.getData().add(new PieChart.Data(etiqueta, entry.getValue()));
                }
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