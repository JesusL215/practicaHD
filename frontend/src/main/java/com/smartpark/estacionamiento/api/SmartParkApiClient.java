package com.smartpark.estacionamiento.api;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.model.domain.Ticket;
import com.smartpark.estacionamiento.model.domain.Usuario;
import com.smartpark.estacionamiento.model.domain.Tarifa;
import com.smartpark.estacionamiento.model.dto.ReporteDashboardDTO;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SmartParkApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final Gson gson;

    public SmartParkApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        // CONFIGURACIÓN DEL ADAPTADOR GSON PARA LOCALDATETIME
        this.gson = new GsonBuilder()

                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                                LocalDateTime.parse(
                                        json.getAsString(),
                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))

                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, type, context) ->
                                new JsonPrimitive(
                                        src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))

                .create();
    }

    public List<ParkingSlot> obtenerTodosLosSlots() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/slots"))
                .GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<ParkingSlot>>(){}.getType());
        }
        throw new Exception("Error al obtener slots: " + response.statusCode());
    }

    public Ticket registrarEntrada(String placa, String tipoVehiculo, Long slotId) throws Exception {
        String url = String.format("%s/tickets/entrada?placa=%s&tipoVehiculo=%s&slotId=%d",
                BASE_URL, placa, tipoVehiculo, slotId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Ticket.class);
        }
        throw new Exception("Error al registrar entrada: " + response.body());
    }

    public Ticket registrarSalida(String placa, boolean conLavado) throws Exception {
        String url = String.format("%s/tickets/salida/placa/%s?conLavado=%b", BASE_URL, placa, conLavado);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Ticket.class);
        }
        throw new Exception("Error al registrar salida: " + response.body());
    }

    public Usuario login(String username, String password) throws Exception {
        String url = String.format("%s/auth/login?username=%s&password=%s", BASE_URL, username, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Usuario.class);
        }
        throw new Exception("Usuario o contraseña incorrectos");
    }

    public ParkingSlot crearSlot(ParkingSlot slot) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/slots"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(slot)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ParkingSlot.class);
        }
        throw new Exception(response.body());
    }

    public ParkingSlot actualizarSlot(Long id, ParkingSlot slot) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/slots/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(slot)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ParkingSlot.class);
        }
        throw new Exception(response.body());
    }

    public void eliminarSlot(Long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/slots/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception(response.body());
        }
    }

    public List<Tarifa> obtenerTodasLasTarifas() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarifas"))
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<Tarifa>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        }
        throw new Exception("Error al obtener tarifas: " + response.body());
    }

    public Tarifa actualizarTarifa(Long id, Tarifa tarifa) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tarifas/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(tarifa)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Tarifa.class);
        }
        throw new Exception("Error al actualizar tarifa: " + response.body());
    }

    public byte[] descargarReciboPdf(Long ticketId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/" + ticketId + "/recibo"))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            return response.body();
        }
        throw new Exception("Error al generar PDF: " + new String(response.body()));
    }

    public List<String> obtenerPlacasActivas() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/activos/placas"))
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(response.body(), listType);
        }
        throw new Exception("Error al obtener placas activas");
    }

    public ReporteDashboardDTO obtenerDatosDashboard() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/reportes/dashboard"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), ReporteDashboardDTO.class);
        }
        throw new Exception("Error al cargar datos del dashboard: " + response.body());
    }

    public List<Ticket> obtenerTodosLosTickets() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets"))
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Type listType = new TypeToken<List<Ticket>>() {}.getType();
            return gson.fromJson(response.body(), listType);
        }

        throw new Exception("Error al obtener el historial de tickets: "
                + response.body());
    }
}