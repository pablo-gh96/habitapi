package miapp.habitapi.repository;



import miapp.habitapi.dto.UserSummary;
import miapp.habitapi.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por nombre
    Optional<User> findByName(String name);

    // Comprobar si un nombre ya existe
    boolean existsByName(String name);
    
 // Sin paginar
    @Query("select new miapp.habitapi.dto.UserSummary(u.id, u.name) " +
           "from User u where u.id <> :excludeId order by u.name asc")
    List<UserSummary> findAllSummariesExcluding(Long excludeId);

    // Paginado (opcional)
    @Query("select new miapp.habitapi.dto.UserSummary(u.id, u.name) " +
           "from User u where u.id <> :excludeId")
    Page<UserSummary> findAllSummariesExcluding(Long excludeId, Pageable pageable);
    
    @Query("select u.name from User u where u.id = :id")
    Optional<String> findNameById(Long id);
    
}

