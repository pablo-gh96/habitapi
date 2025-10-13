package miapp.habitapi.repository;



import miapp.habitapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por nombre
    Optional<User> findByName(String name);

    // Comprobar si un nombre ya existe
    boolean existsByName(String name);
}

