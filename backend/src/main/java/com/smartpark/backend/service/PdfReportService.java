package com.smartpark.backend.service;

// ¡AQUÍ ESTÁ LA MAGIA! Todas las importaciones actualizadas a OpenPDF 3+
import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;

import com.smartpark.backend.model.domain.Ticket;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfReportService {

    private static final Font TITULO_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font TEXTO_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

    public byte[] generarReciboPdf(Ticket ticket) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Encabezado
            Paragraph header = new Paragraph("SMARTPARK - TICKET DE SALIDA\n\n", TITULO_FONT);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            // Sanitización. Limpiamos cualquier carácter extraño de la placa
            String placaLimpia = sanitizarTexto(ticket.getVehiculo().getPlaca());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            // Obtenemos el tipo de vehículo leyendo el nombre de la clase hija
            String tipoVehiculo = ticket.getVehiculo().getClass().getSimpleName().toUpperCase();

            document.add(new Paragraph("ID Ticket: " + ticket.getId(), TEXTO_FONT));
            document.add(new Paragraph("Placa: " + placaLimpia, BOLD_FONT));
            document.add(new Paragraph("Tipo de Vehículo: " + tipoVehiculo, TEXTO_FONT));
            document.add(new Paragraph("Espacio: " + ticket.getParkingSlot().getNumero(), TEXTO_FONT));
            document.add(new Paragraph("Hora Entrada: " + ticket.getHoraEntrada().format(formatter), TEXTO_FONT));
            document.add(new Paragraph("Hora Salida: " + ticket.getHoraSalida().format(formatter), TEXTO_FONT));

            document.add(new Paragraph("\n--------------------------------------------------\n", TEXTO_FONT));

            Paragraph total = new Paragraph("TOTAL A PAGAR: S/ " + String.format("%.2f", ticket.getCostoTotal()), TITULO_FONT);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            // Liberar recursos del Document
            document.close();

            return baos.toByteArray(); // Retornamos el archivo como bytes binarios
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el documento PDF: " + e.getMessage());
        }
    }

    // Sanitización de Datos para evitar saltos de línea maliciosos
    private String sanitizarTexto(String input) {
        if (input == null) return "N/A";
        // Elimina retornos de carro, tabulaciones y caracteres invisibles extraños
        return input.replaceAll("[\\n\\r\\t]", "").trim().toUpperCase();
    }
}