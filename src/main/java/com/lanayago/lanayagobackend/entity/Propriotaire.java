package com.lanayago.lanayagobackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Propriotaire extends User {

    @ManyToOne
    @JoinColumn(name = "engin_id")
    private Engin engin;
}
