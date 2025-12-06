package com.lanayago.lanayagobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour les opérations spécifiques aux conducteurs
 *
 * Endpoints à implémenter:
 * - GET /api/conducteur/profile - Obtenir le profil du conducteur
 * - PUT /api/conducteur/profile - Mettre à jour le profil
 * - PUT /api/conducteur/disponibilite - Changer la disponibilité
 * - GET /api/conducteur/courses - Historique des courses
 * - GET /api/conducteur/statistiques - Statistiques du conducteur
 * - POST /api/conducteur/position - Mettre à jour la position GPS
 */
@RestController
@RequestMapping("/api/conducteur")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONDUCTEUR')")
public class ConducteurController {

    // TODO: Ajouter les endpoints spécifiques aux conducteurs

    /**
     * Exemple d'endpoint à implémenter:
     *
     * @GetMapping("/profile")
     * public ResponseEntity<ConducteurDTO> getProfile() {
     *     // Logique pour récupérer le profil
     * }
     */
}