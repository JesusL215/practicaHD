package com.smartpark.backend.util;

import com.smartpark.backend.model.domain.ParkingSlot;
import com.smartpark.backend.model.domain.Usuario;
import com.smartpark.backend.repository.ParkingSlotRepository;
import com.smartpark.backend.repository.UsuarioRepository;
import com.smartpark.backend.model.domain.Tarifa;
import com.smartpark.backend.repository.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ParkingSlotRepository slotRepository;
    private final PasswordEncoder passwordEncoder;
    private final TarifaRepository tarifaRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Crear usuarios con CONTRASEÑAS ENCRIPTADAS
        if (usuarioRepository.count() == 0) {
            System.out.println("Seeder: Creando usuarios seguros...");

            usuarioRepository.save(new Usuario(null, "admin", passwordEncoder.encode("admin123"), "ADMIN"));
            usuarioRepository.save(new Usuario(null, "operador", passwordEncoder.encode("operador123"), "OPERADOR"));
        }

        // 2. Insertar slots
        if (slotRepository.count() == 0) {
            ParkingSlot s1 = new ParkingSlot(null, "A1", "DISPONIBLE", "AUTO", null);
            ParkingSlot s2 = new ParkingSlot(null, "A2", "DISPONIBLE", "AUTO", null);
            ParkingSlot s3 = new ParkingSlot(null, "A3", "DISPONIBLE", "AUTO", null);
            ParkingSlot s4 = new ParkingSlot(null, "A4", "DISPONIBLE", "AUTO", null);
            ParkingSlot s5 = new ParkingSlot(null, "A5", "DISPONIBLE", "AUTO", null);
            ParkingSlot s6 = new ParkingSlot(null, "A6", "DISPONIBLE", "AUTO", null);

            ParkingSlot s7 = new ParkingSlot(null, "M1", "DISPONIBLE", "MOTO", null);
            ParkingSlot s8 = new ParkingSlot(null, "M2", "DISPONIBLE", "MOTO", null);
            ParkingSlot s9 = new ParkingSlot(null, "M3", "DISPONIBLE", "MOTO", null);
            ParkingSlot s10 = new ParkingSlot(null, "M4", "DISPONIBLE", "MOTO", null);
            ParkingSlot s11 = new ParkingSlot(null, "M5", "DISPONIBLE", "MOTO", null);
            ParkingSlot s12 = new ParkingSlot(null, "M6", "DISPONIBLE", "MOTO", null);

            slotRepository.saveAll(java.util.List.of(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12));

            System.out.println("Espacios de estacionamiento creados exitosamente por el Seeder.");
        }

        if (tarifaRepository.count() == 0) {
            System.out.println("Seeder: Configurando tarifas iniciales...");

            tarifaRepository.save(new Tarifa(null, "TARIFA_AUTO", "Costo por hora - Automóvil", 5.0));
            tarifaRepository.save(new Tarifa(null, "TARIFA_MOTO", "Costo por hora - Motocicleta", 3.0));
            tarifaRepository.save(new Tarifa(null, "SERVICIO_LAVADO", "Servicio adicional de lavado de vehículo", 20.0));
        }
    }
}