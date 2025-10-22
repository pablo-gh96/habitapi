package com.apphabit.backend.repositories;


import com.apphabit.backend.models.CommentResponse;
import com.apphabit.backend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByToUserIdAndTargetDateOrderByCreatedAtAsc(Long userId, LocalDate targetDate);
	
	@Query("""
		      select new com.apphabit.backend.models.CommentResponse(
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
	
	@Query("SELECT COUNT(c) FROM Comment c WHERE c.targetDate = :day")
    long countByDay(@Param("day") LocalDate day);
}
