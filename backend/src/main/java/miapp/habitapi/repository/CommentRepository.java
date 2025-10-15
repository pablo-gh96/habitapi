package miapp.habitapi.repository;

import miapp.habitapi.dto.CommentResponse;
import miapp.habitapi.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByToUserIdAndTargetDateOrderByCreatedAtAsc(Long userId, LocalDate targetDate);
	
	@Query("""
		      select new miapp.habitapi.dto.CommentResponse(
		        c.id,
		        c.message,
		        c.createdAt,
		        c.targetDate,
		        fu.name,
		        tu.name
		      )
		      from Comment c
		      join c.fromUser fu
		      join c.toUser tu
		      where c.toUser.id = :userId
		        and c.targetDate = :day
		      order by c.createdAt asc
		    """)
		    List<CommentResponse> findDayCommentsForUser(Long userId, LocalDate day);
}
