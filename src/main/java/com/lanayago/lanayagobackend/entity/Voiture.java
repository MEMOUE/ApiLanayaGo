package com.lanayago.lanayagobackend.entity;

import com.lanayago.lanayagobackend.entity.enums.TypeVehicule;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("VOITURE")
public class Voiture extends Engin {

    @Enumerated(EnumType.STRING)
    private TypeVehicule typeVehicule;

    private Integer nombrePlaces;

    private Double capaciteChargement; // en kg
}