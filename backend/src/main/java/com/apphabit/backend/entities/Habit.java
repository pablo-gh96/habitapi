package com.apphabit.backend.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título del hábito
    @Column(nullable = false, length = 120)
    private String title;

    // Emoji o nombre corto (máx. unos pocos chars)
    @Column(nullable = false, length = 16)
    private String icon;

    // Estado (usa tu enum Status: UNDEFINED, DONE, PARTIALLY, NOT_DONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.UNDEFINED;

    // Fecha concreta de la ocurrencia
    @Column(nullable = false)
    private LocalDate date;

    // Muchos hábitos pertenecen a un usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // evita LazyInitializationException al serializar
    private User user;

    // --- Constructores ---
    public Habit() {}

    public Habit(String title, String icon, Status status, LocalDate date) {
        this.title = title;
        this.icon = icon;
        this.status = status;
        this.date = date;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // --- Exponer userId en JSON (opcional y útil para el front) ---
    @Transient
    @JsonProperty("userId")
    public Long getUserIdForJson() {
        // Leer el id del proxy es seguro sin inicializar toda la entidad
        return (user != null) ? user.getId() : null;
    }
    
    public enum Status {
        DONE,
        PARTIALLY,
        NOT_DONE,
        UNDEFINED;
        
        @JsonValue
        public String toLowerCase() {
            return this.name().toLowerCase();
        }
        
    }
}

