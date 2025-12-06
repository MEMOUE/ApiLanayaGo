package com.lanayago.lanayagobackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "evaluateur_id")
    private User evaluateur; // Client ou Conducteur

    @ManyToOne
    @JoinColumn(name = "evalue_id")
    private User evalue;

    @Column(nullable = false)
    private Integer note; // 1 à 5 étoiles

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @CreationTimestamp
    @Column(name = "date_evaluation")
    private LocalDateTime dateEvaluation;
}