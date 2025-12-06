package com.lanayago.lanayagobackend.entity;

import com.lanayago.lanayagobackend.entity.enums.StatutEngin;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "engins")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_engin", discriminatorType = DiscriminatorType.STRING)
public abstract class Engin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    @Column(nullable = false)
    private String couleur;

    @Column(nullable = false, unique = true)
    private String matricule;

    private Integer annee;

    @Column(columnDefinition = "TEXT")
    private String photoEngin; // URL ou chemin du fichier

    @ManyToOne(optional = false)
    @JoinColumn(name = "proprietaire_id")
    private Propriotaire propriotaire;

    @Enumerated(EnumType.STRING)
    private StatutEngin statut = StatutEngin.DISPONIBLE;

    private boolean active = true;
}