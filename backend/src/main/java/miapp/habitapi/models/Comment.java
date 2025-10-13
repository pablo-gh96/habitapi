package miapp.habitapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Texto del comentario
    @Column(nullable = false, length = 1000)
    private String message;

    // Fecha-hora real de creación (cuando se envía)
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Día al que se refiere el comentario (ej. 2025-10-13)
    @Column(nullable = false)
    private LocalDate targetDate;

    // Emisor
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    @JsonIgnore
    private User fromUser;

    // Receptor
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    @JsonIgnore
    private User toUser;

    public Comment() {}

    public Comment(String message, LocalDate targetDate, User fromUser, User toUser) {
        this.message = message;
        this.targetDate = targetDate;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // Getters/Setters
    public Long getId() { return id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public User getFromUser() { return fromUser; }
    public void setFromUser(User fromUser) { this.fromUser = fromUser; }

    public User getToUser() { return toUser; }
    public void setToUser(User toUser) { this.toUser = toUser; }

    // Exponer IDs en JSON
    @Transient
    @JsonProperty("fromUserId")
    public Long getFromUserIdForJson() { return (fromUser != null) ? fromUser.getId() : null; }

    @Transient
    @JsonProperty("toUserId")
    public Long getToUserIdForJson() { return (toUser != null) ? toUser.getId() : null; }
}
