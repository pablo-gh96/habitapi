package miapp.habitapi.repository;

import miapp.habitapi.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByToUserIdAndTargetDateOrderByCreatedAtAsc(Long userId, LocalDate targetDate);
}
