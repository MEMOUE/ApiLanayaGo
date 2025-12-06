package com.lanayago.lanayagobackend.entity;

import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.entity.enums.TypeCourse;
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
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCourse typeCourse;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne
    @JoinColumn(name = "conducteur_id")
    private Conducteur conducteur;

    @ManyToOne
    @JoinColumn(name = "engin_id")
    private Engin engin;

    // Adresses
    @Column(nullable = false)
    private String adresseDepart;

    private Double latitudeDepart;
    private Double longitudeDepart;

    @Column(nullable = false)
    private String adresseArrivee;

    private Double latitudeArrivee;
    private Double longitudeArrivee;

    // Informations colis (pour livraison)
    @Column(columnDefinition = "TEXT")
    private String descriptionColis;

    private Double poidsColis;

    // Informations passager (pour transport)
    private Integer nombrePassagers;

    // Tarification
    private Double distanceKm;
    private Double montantEstime;
    private Double montantFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCourse statut = StatutCourse.EN_ATTENTE;

    @CreationTimestamp
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @OneToOne(mappedBy = "course", cascade = CascadeType.ALL)
    private Evaluation evaluation;

    @Column(columnDefinition = "TEXT")
    private String noteClient; // Notes du client pour le conducteur
}