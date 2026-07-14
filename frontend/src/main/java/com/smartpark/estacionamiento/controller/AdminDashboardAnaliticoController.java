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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

public class AdminDashboardAnaliticoController {

    @FXML private Label lblIngresosHoy;
    @FXML private Label lblIngresadosHoy;
    @FXML private Label lblEstacionados;
    @FXML private Label lblLibres;

    @FXML private BarChart<String, Number> ingresosBarChart;
    @FXML private PieChart tipoVehiculoPieChart;
    @FXML private BarChart<String, Number> horasPicoBarChart;

    @FXML private TableView<MovimientoDTO> movimientosTable;
    @FXML private TableColumn<MovimientoDTO, String> colFecha;
    @FXML private TableColumn<MovimientoDTO, String> colHora;
    @FXML private TableColumn<MovimientoDTO, String> colPlaca;
    @FXML private TableColumn<MovimientoDTO, String> colTipo;
    @FXML private TableColumn<MovimientoDTO, String> colEstado;
    @FXML private TableColumn<MovimientoDTO, Double> colMonto;

    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private ComboBox<String> tipoVehiculoCombo;

    private SmartParkApiClient apiClient;

    @FXML
    public void initialize() {
        this.apiClient = new SmartParkApiClient();

        // 1. ACTIVAR COMBOS Y FECHAS POR DEFECTO
        tipoVehiculoCombo.getItems().addAll("Todos", "AUTO", "MOTO");
        tipoVehiculoCombo.setValue("Todos");
        fechaInicioPicker.setValue(LocalDate.now().minusDays(7)); // Últimos 7 días
        fechaFinPicker.setValue(LocalDate.now());

        // 2. CONFIGURAR TABLA
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
            // Capturamos visualmente los filtros para la siguiente fase
            String tipoFiltro = tipoVehiculoCombo.getValue();
            LocalDate inicio = fechaInicioPicker.getValue();
            LocalDate fin = fechaFinPicker.getValue();
            System.out.println("Filtros aplicados en UI -> Tipo: " + tipoFiltro + " | Rango: " + inicio + " a " + fin);

            ReporteDashboardDTO reporte = apiClient.obtenerDatosDashboard();

            lblIngresosHoy.setText(String.format("S/ %.2f", reporte.getIngresosHoy()));
            lblIngresadosHoy.setText(String.valueOf(reporte.getVehiculosIngresadosHoy()));
            lblEstacionados.setText(String.valueOf(reporte.getVehiculosEstacionados()));
            lblLibres.setText(String.valueOf(reporte.getEspaciosLibres()));

            ingresosBarChart.getData().clear();
            XYChart.Series<String, Number> seriesIngresos = new XYChart.Series<>();
            if (reporte.getIngresosPorDia() != null) {
                for (Map.Entry<String, Double> entry : reporte.getIngresosPorDia().entrySet()) {
                    seriesIngresos.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
            }
            ingresosBarChart.getData().add(seriesIngresos);

            tipoVehiculoPieChart.getData().clear();
            if (reporte.getIngresosPorTipoVehiculo() != null) {
                for (Map.Entry<String, Double> entry : reporte.getIngresosPorTipoVehiculo().entrySet()) {
                    String etiqueta = String.format("%s (S/ %.2f)", entry.getKey(), entry.getValue());
                    tipoVehiculoPieChart.getData().add(new PieChart.Data(etiqueta, entry.getValue()));
                }
            }

            horasPicoBarChart.getData().clear();
            XYChart.Series<String, Number> seriesHoras = new XYChart.Series<>();
            if (reporte.getHorasPico() != null) {
                for (Map.Entry<String, Integer> entry : reporte.getHorasPico().entrySet()) {
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

    @FXML
    private void exportarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Movimientos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo CSV (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(movimientosTable.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Fecha;Hora;Placa;Tipo Vehiculo;Estado;Monto Pagado");

                for (MovimientoDTO mov : movimientosTable.getItems()) {
                    writer.printf("%s;%s;%s;%s;%s;%.2f\n",
                            mov.getFecha(),
                            mov.getHora(),
                            mov.getPlaca(),
                            mov.getTipoVehiculo(),
                            mov.getEstado(),
                            mov.getMontoPagado());
                }

                mostrarAlerta("Exportación Exitosa", "El reporte se ha guardado correctamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Error de Exportación", "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }

    private void mostrarAlerta(String titulo, String contenido) {
        mostrarAlerta(titulo, contenido, Alert.AlertType.ERROR);
    }
}