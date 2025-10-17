package com.apphabit.backend.services;


import com.apphabit.backend.models.CommentResponse;
import com.apphabit.backend.entities.Comment;
import com.apphabit.backend.entities.User;
import com.apphabit.backend.repositories.CommentRepository;
import com.apphabit.backend.repositories.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    
    public CommentService(CommentRepository commentRepo, UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    public List<CommentResponse> getCommentsForDay(Long userId, LocalDate day) {
        if (userId == null) throw new IllegalArgumentException("userId is required");
        if (day == null) throw new IllegalArgumentException("day is required");
        return commentRepo.findDayCommentsForUser(userId, day);
    }
    
    @Transactional
    public Comment create(Long toUserId, LocalDate targetDate, String message) {
        if (toUserId == null) {
            throw new IllegalArgumentException("toUserId are required");
        }
        if (targetDate == null) {
            throw new IllegalArgumentException("targetDate (day) is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
        
        User from = userRepo.findById(getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("fromUser not found: " + getUser().getId()));
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
    
    private CommentResponse toResponse(Comment c) {
        return new CommentResponse(
            c.getId(),
            c.getMessage(),
            c.getCreatedAt(),
            c.getTargetDate(),
            c.getFromUser().getName(),
            c.getToUser().getName()
        );
    }
    
    private User getUser() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	String username = (String) auth.getPrincipal();
    	return userRepo.findByUsername(username)
    	        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}

