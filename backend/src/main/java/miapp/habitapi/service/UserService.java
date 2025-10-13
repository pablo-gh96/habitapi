package miapp.habitapi.service;

import java.util.Optional;

import miapp.habitapi.models.User;
import miapp.habitapi.repository.UserRepository;

public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User createAccount(User user) {
        if (user.getName() == null || user.getName().isBlank() ||
            user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Nombre y contraseña son obligatorios");
        }

        if (repo.existsByName(user.getName())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        return repo.save(user);
    }

    // Login simple
    public Optional<User> login(User user) {
        return repo.findByName(user.getName())
                .filter(u -> u.getPassword().equals(user.getPassword()));
    }
}
