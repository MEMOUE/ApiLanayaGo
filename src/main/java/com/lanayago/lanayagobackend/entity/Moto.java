package com.lanayago.lanayagobackend.entity;

import com.lanayago.lanayagobackend.entity.enums.TypeMoto;
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
@DiscriminatorValue("MOTO")
public class Moto extends Engin {

    private Integer cylindree; // 125cc, 250cc, etc.

    @Enumerated(EnumType.STRING)
    private TypeMoto typeMoto;
}