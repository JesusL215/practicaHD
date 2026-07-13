package com.smartpark.backend.controller;

import com.smartpark.backend.model.domain.Usuario;
import com.smartpark.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Inyectamos el verificador

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String username,
            @RequestParam String password) {

        Optional<Usuario> userOpt = usuarioRepository.findByUsername(username);

        // Verificamos si existe el usuario Y si la contraseña que escribió coincide con el hash encriptado
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return ResponseEntity.ok(userOpt.get());
        }

        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }
}