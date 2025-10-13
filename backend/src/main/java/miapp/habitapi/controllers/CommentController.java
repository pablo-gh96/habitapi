package miapp.habitapi.controllers;

import miapp.habitapi.dto.CreateCommentRequest;
import miapp.habitapi.models.Comment;
import miapp.habitapi.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * GET /api/comments/day?userId=1&day=2025-10-13
     * Devuelve comentarios (más recientes primero) donde el usuario participa
     * como emisor o receptor durante ese día.
     */
    @GetMapping("/day")
    public ResponseEntity<?> getCommentsForDay(
            @RequestParam Long userId,
            @RequestParam LocalDate day
    ) {
        try {
            List<Comment> data = commentService.getCommentsForDay(userId, day);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    /**
     * POST /api/comments
     * Body (JSON):
     * {
     *   "message": "texto",
     *   "fromUserId": 1,
     *   "toUserId": 2,
     *   "day": "2025-10-13"
     * }
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateCommentRequest body) {
        try {
            Comment saved = commentService.create(
                    body.getFromUserId(),
                    body.getToUserId(),
                    body.getDay(),
                    body.getMessage()
            );
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }


}
