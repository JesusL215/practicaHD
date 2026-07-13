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

    // CORRECCIÓN: Tipamos el BarChart
    @FXML private BarChart<String, Number> ingresosBarChart;
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
            ReporteDashboardDTO reporte = apiClient.obtenerDatosDashboard();

            ingresosBarChart.getData().clear();

            // CORRECCIÓN: Tipamos la Serie del gráfico
            XYChart.Series<String, Number> series = new XYChart.Series<>();

            if (reporte.getIngresosPorDia() != null) {
                // CORRECCIÓN: Tipamos el Map.Entry
                for (Map.Entry<String, Double> entry : reporte.getIngresosPorDia().entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            ingresosBarChart.getData().add(series);

            tipoVehiculoPieChart.getData().clear();
            if (reporte.getIngresosPorTipoVehiculo() != null) {
                // CORRECCIÓN: Tipamos el Map.Entry
                for (Map.Entry<String, Double> entry : reporte.getIngresosPorTipoVehiculo().entrySet()) {
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