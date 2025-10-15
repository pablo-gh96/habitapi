// src/main/java/miapp/habitapi/dto/CommentResponse.java
package miapp.habitapi.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private LocalDate targetDate;

    private String fromUserName;
    private String toUserName;

    public CommentResponse(Long id, String message, LocalDateTime createdAt, LocalDate targetDate,
                           String fromUserName, String toUserName) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.targetDate = targetDate;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDate getTargetDate() { return targetDate; }
    public String getFromUserName() { return fromUserName; }
    public String getToUserName() { return toUserName; }
}
