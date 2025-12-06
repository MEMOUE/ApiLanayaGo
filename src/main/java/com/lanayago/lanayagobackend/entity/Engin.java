package com.lanayago.lanayagobackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Engin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false)
    private String marque;
    @Column(nullable = false)
    private String couleur;
    @Column(nullable = false)
    private String matricule;

    @ManyToOne(optional = false)
    private Propriotaire propriotaire;

}
