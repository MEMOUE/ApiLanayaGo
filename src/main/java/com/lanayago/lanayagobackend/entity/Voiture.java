package com.lanayago.lanayagobackend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;

public class Voiture extends Engin {

    @OneToOne(cascade = CascadeType.ALL)
    private TypeVehicule vehicule;
}
