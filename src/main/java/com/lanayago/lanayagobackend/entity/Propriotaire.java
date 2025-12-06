package com.lanayago.lanayagobackend.entity;

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
@Table(name = "proprietaires")
public class Propriotaire extends User {

    @OneToMany(mappedBy = "propriotaire", cascade = CascadeType.ALL)
    private List<Engin> engins = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String documentIdentite; // CNI, Passeport (URL ou chemin)

    private boolean verified = false;
}