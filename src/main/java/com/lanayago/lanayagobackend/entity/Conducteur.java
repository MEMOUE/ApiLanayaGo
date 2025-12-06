package com.lanayago.lanayagobackend.entity;

import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conducteurs")
public class Conducteur extends User {

    @Column(length = 15, nullable = false, unique = true)
    private String numPermis;

    @Enumerated(EnumType.STRING)
    private StatutConducteur statut = StatutConducteur.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String photoPermis; // URL ou chemin du fichier

    private Double noteGlobale = 0.0;

    private Integer nombreCourses = 0;

    @OneToMany(mappedBy = "conducteur")
    private List<Course> courses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "engin_actuel_id")
    private Engin enginActuel;
}