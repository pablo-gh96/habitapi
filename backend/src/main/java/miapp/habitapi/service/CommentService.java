package miapp.habitapi.service;

import miapp.habitapi.models.Comment;
import miapp.habitapi.models.User;
import miapp.habitapi.repository.CommentRepository;
import miapp.habitapi.repository.UserRepository;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    
    public CommentService(CommentRepository commentRepo, UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    /**
     * Comentarios donde participa el usuario (como emisor o receptor) en un día concreto.
     * Ordenados del más antiguo al más reciente.
     */
    public List<Comment> getCommentsForDay(Long userId, LocalDate day) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (day == null) throw new IllegalArgumentException("day is required");
        return commentRepo.findByToUserIdAndTargetDateOrderByCreatedAtAsc(userId, day);
    }
    
    @Transactional
    public Comment create(Long fromUserId, Long toUserId, LocalDate targetDate, String message) {
        if (fromUserId == null || toUserId == null) {
            throw new IllegalArgumentException("fromUserId and toUserId are required");
        }
        if (targetDate == null) {
            throw new IllegalArgumentException("targetDate (day) is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }

        User from = userRepo.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("fromUser not found: " + fromUserId));
        User to = userRepo.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("toUser not found: " + toUserId));

        Comment c = new Comment();
        c.setMessage(message.trim());
        c.setFromUser(from);
        c.setToUser(to);
        c.setTargetDate(targetDate); // ⬅️ guardar el día objetivo
        // createdAt se rellena con @PrePersist en la entidad
        return commentRepo.save(c);
    }
}
