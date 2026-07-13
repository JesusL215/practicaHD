package com.smartpark.backend.util;

import com.smartpark.backend.model.domain.ParkingSlot;
import com.smartpark.backend.model.domain.Usuario;
import com.smartpark.backend.repository.ParkingSlotRepository;
import com.smartpark.backend.repository.UsuarioRepository;
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
            System.out.println("Seeder: Insertando espacios de estacionamiento...");
            slotRepository.save(new ParkingSlot(null, "A1", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "A2", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "A3", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "A4", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "A5", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "A6", "DISPONIBLE", "AUTO"));
            slotRepository.save(new ParkingSlot(null, "M1", "DISPONIBLE", "MOTO"));
            slotRepository.save(new ParkingSlot(null, "M2", "DISPONIBLE", "MOTO"));
            slotRepository.save(new ParkingSlot(null, "M3", "DISPONIBLE", "MOTO"));
            slotRepository.save(new ParkingSlot(null, "M4", "DISPONIBLE", "MOTO"));
            slotRepository.save(new ParkingSlot(null, "M5", "DISPONIBLE", "MOTO"));
            slotRepository.save(new ParkingSlot(null, "M6", "DISPONIBLE", "MOTO"));

        }
    }
}