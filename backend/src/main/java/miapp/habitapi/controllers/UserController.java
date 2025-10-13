package miapp.habitapi.controllers;


import miapp.habitapi.models.User;
import miapp.habitapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // 🧾 Crear cuenta
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User created = service.createAccount(user);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuario creado correctamente"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 🔐 Login simple
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return service.login(user)
                .map(u -> ResponseEntity.ok(Map.of(
                        "user", u.getName(),
                        "id", u.getId()
                )))
                .orElseGet(() ->
                        ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"))
                );
    }
}

