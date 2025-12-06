package com.lanayago.lanayagobackend.dto;

import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.entity.enums.TypeCourse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CourseDTO {

    // Requête de création
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseRequest {
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

        private String noteClient;
    }

    // Réponse
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CourseResponse {
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
        private EnginDTO engin;
        private LocalDateTime dateCreation;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private String descriptionColis;
        private Integer nombrePassagers;
    }

    // DTO utilisateur simplifié
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        private Double noteGlobale;
    }

    // DTO engin simplifié
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EnginDTO {
        private Long id;
        private String marque;
        private String modele;
        private String couleur;
        private String matricule;
    }
}