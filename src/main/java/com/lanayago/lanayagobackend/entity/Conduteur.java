package com.lanayago.lanayagobackend.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Conduteur extends User{

    @Column(length = 15, nullable = false)
    private String numpermis;
    @Column(nullable = false)
    private boolean is_active = false;

}
