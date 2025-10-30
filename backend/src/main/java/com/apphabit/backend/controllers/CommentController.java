package com.apphabit.backend.controllers;



import com.apphabit.backend.models.CommentResponse;
import com.apphabit.backend.models.CreateCommentRequest;
import com.apphabit.backend.entities.Comment;
import com.apphabit.backend.services.CommentService;
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

    @GetMapping("/day")
    public ResponseEntity<?> getCommentsForDay(
            @RequestParam Long userId,
            @RequestParam LocalDate day
    ) {
        try {
            List<CommentResponse> data = commentService.getCommentsForDay(userId, day);
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
                    body.getToUserId(),
                    body.getDay(),
                    body.getMessage()
            );
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/count")
    public long getNumberOfComments(@RequestParam Long userId, @RequestParam LocalDate day) {
        return commentService.getNumberOfComments(userId, day);
    }

}

