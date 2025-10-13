package miapp.habitapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import miapp.habitapi.dto.UserSummary;
import miapp.habitapi.models.User;
import miapp.habitapi.repository.UserRepository;

@Service
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
    
    public List<UserSummary> listOthers(Long myId) {
        if (myId == null) throw new IllegalArgumentException("myId is required");
        return repo.findAllSummariesExcluding(myId);
    }

    // Variante paginada (por si la quieres)
    public List<UserSummary> listOthers(Long myId, int page, int size) {
        if (myId == null) throw new IllegalArgumentException("myId is required");
        return repo.findAllSummariesExcluding(myId, PageRequest.of(page, size)).getContent();
    }
}
