package com.lanayago.lanayagobackend.dto.reponse;

import com.lanayago.lanayagobackend.entity.enums.Roles;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConducteurProfileDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Roles role;
    private String numPermis;
    private String photoPermis;
    private StatutConducteur statut;
    private Double noteGlobale;
    private Integer nombreCourses;
    private EnginSimpleDTO enginActuel;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnginSimpleDTO {
        private Long id;
        private String marque;
        private String modele;
        private String couleur;
        private String matricule;
    }
}