package miapp.habitapi.dto;

import java.time.LocalDate;

public class CreateHabit {

    private String title;       // "Título"
    private String icon;        // "Icono" (emoji o nombre corto)
    private LocalDate date;     // "Fecha" (YYYY-MM-DD)
    private RepeatType repeat;  // "once" | "daily" | "weekly" | "monthly"

    // ⬇️ Referencia al usuario que crea el hábito
    private Long userId;

    public CreateHabit() {}

    // --- Getters/Setters ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; } // <- corregido

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public RepeatType getRepeat() { return repeat; }
    public void setRepeat(RepeatType repeat) { this.repeat = repeat; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
