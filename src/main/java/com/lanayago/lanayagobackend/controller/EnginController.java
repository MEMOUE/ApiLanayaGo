package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.CourseDTO;
import com.lanayago.lanayagobackend.dto.request.EnginRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion des engins (véhicules)
 *
 * Endpoints à implémenter:
 * - POST /api/engins - Créer un nouvel engin
 * - GET /api/engins - Lister mes engins
 * - GET /api/engins/{id} - Obtenir un engin spécifique
 * - PUT /api/engins/{id} - Modifier un engin
 * - DELETE /api/engins/{id} - Supprimer un engin
 * - PUT /api/engins/{id}/conducteur - Assigner un conducteur
 * - PUT /api/engins/{id}/statut - Changer le statut (disponible, maintenance, etc.)
 */
@RestController
@RequestMapping("/api/engins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROPRIETAIRE')")
public class EnginController {

    // TODO: Ajouter les endpoints pour la gestion des engins

    /**
     * Exemple d'endpoint à implémenter:
     *
     * */
      @PostMapping
      public ResponseEntity<CourseDTO.EnginDTO> createEngin(@Valid @RequestBody EnginRequest request) {
          // Logique pour créer un engin
          return null;
      }

      @GetMapping
      public ResponseEntity<List<CourseDTO.EnginDTO>> getMesEngins() {
          // Logique pour récupérer les engins du propriétaire
          return null;
      }

}