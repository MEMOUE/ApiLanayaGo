package com.lanayago.lanayagobackend.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDTO {

    // Statistiques utilisateurs
    private Long totalUtilisateurs;
    private Long totalConducteurs;
    private Long conducteursApprouves;
    private Long conducteursEnAttente;

    // Statistiques courses
    private Long totalCourses;
    private Long coursesEnAttente;
    private Long coursesEnCours;
    private Long coursesTerminees;
    private Long coursesRecentes; // 30 derniers jours

    // Statistiques engins
    private Long totalEngins;

    // Statistiques financières
    private Double revenusMois;
}