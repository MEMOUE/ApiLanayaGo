package com.lanayago.lanayagobackend.dto.reponse;

import com.lanayago.lanayagobackend.dto.UserDTO;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.entity.enums.TypeCourse;
import lombok.Data;

@Data
public class CourseResponse {
    private Long id;
    private TypeCourse typeCourse;
    private String adresseDepart;
    private String adresseArrivee;
    private Double distanceKm;
    private Double montantEstime;
    private Double montantFinal;
    private StatutCourse statut;
    private UserDTO client;
    private UserDTO conducteur;
    private String dateCreation;
}
