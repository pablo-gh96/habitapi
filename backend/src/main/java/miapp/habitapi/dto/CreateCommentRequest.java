package miapp.habitapi.dto;




import java.time.LocalDate;

public class CreateCommentRequest {


 private String message;


 private Long fromUserId;


 private Long toUserId;

 /** Día al que se refiere el comentario (ej. 2025-10-13) */

 private LocalDate day;

 // --- getters / setters ---
 public String getMessage() { return message; }
 public void setMessage(String message) { this.message = message; }

 public Long getFromUserId() { return fromUserId; }
 public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }

 public Long getToUserId() { return toUserId; }
 public void setToUserId(Long toUserId) { this.toUserId = toUserId; }

 public LocalDate getDay() { return day; }
 public void setDay(LocalDate day) { this.day = day; }
}

