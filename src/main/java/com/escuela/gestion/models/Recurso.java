package com.escuela.gestion.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

// Esta entidad representa la "Biblioteca": aquí se guardan los materiales de apoyo.
// Puede ser un simple enlace (link) o un archivo real (PDF/Imagen).
@Entity
@Table(name = "recursos")
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String url;
    private String tipo; // Define si es 'link' (enlace web) o 'file' (archivo subido)

    // ¡Importante! Aquí guardamos el archivo PDF convertido en una cadena de texto larga.
    // Usamos columnDefinition="TEXT" para permitir guardar textos muy grandes.
    @Column(name = "archivo_base64", columnDefinition = "TEXT")
    private String archivoBase64; // <--- NUEVO CAMPO PARA EL PDF
    
    // Guarda automáticamente la fecha y hora en que el profe subió el material.
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Conecta este material con un curso específico (para que no salga en otras materias).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Asignacion asignacion;

    // Getters y Setters: Necesarios para que la aplicación lea y escriba estos datos.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getArchivoBase64() { return archivoBase64; }
    public void setArchivoBase64(String archivoBase64) { this.archivoBase64 = archivoBase64; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Asignacion getAsignacion() { return asignacion; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
}