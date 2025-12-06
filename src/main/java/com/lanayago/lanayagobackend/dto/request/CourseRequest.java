package com.lanayago.lanayagobackend.dto.request;

import com.lanayago.lanayagobackend.entity.enums.TypeCourse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequest {

    @NotNull(message = "Le type de course est obligatoire")
    private TypeCourse typeCourse;

    @NotBlank(message = "L'adresse de départ est obligatoire")
    private String adresseDepart;

    @NotNull
    private Double latitudeDepart;

    @NotNull
    private Double longitudeDepart;

    @NotBlank(message = "L'adresse d'arrivée est obligatoire")
    private String adresseArrivee;

    @NotNull
    private Double latitudeArrivee;

    @NotNull
    private Double longitudeArrivee;

    // Pour livraison
    private String descriptionColis;
    private Double poidsColis;

    // Pour transport de personnes
    private Integer nombrePassagers;
}



