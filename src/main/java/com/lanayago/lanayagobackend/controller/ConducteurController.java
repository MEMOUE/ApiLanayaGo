package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.reponse.ConducteurProfileDTO;
import com.lanayago.lanayagobackend.dto.reponse.CourseResponse;
import com.lanayago.lanayagobackend.service.ConducteurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conducteur")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CONDUCTEUR')")
@Tag(name = "Conducteur", description = "API pour les opérations spécifiques aux conducteurs")
public class ConducteurController {

    private final ConducteurService conducteurService;

    @GetMapping("/profile")
    @Operation(
            summary = "Obtenir le profil du conducteur",
            description = "Récupère les informations complètes du profil du conducteur connecté",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profil récupéré avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - réservé aux conducteurs")
    })
    public ResponseEntity<ConducteurProfileDTO> getProfile() {
        return ResponseEntity.ok(conducteurService.getProfile());
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Mettre à jour le profil",
            description = "Met à jour les informations du profil du conducteur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profil mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ConducteurProfileDTO> updateProfile(
            @RequestBody ConducteurProfileDTO profileDTO) {
        return ResponseEntity.ok(conducteurService.updateProfile(profileDTO));
    }

    @PutMapping("/disponibilite")
    @Operation(
            summary = "Changer la disponibilité",
            description = "Met à jour le statut de disponibilité du conducteur pour recevoir des courses",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Disponibilité mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ConducteurProfileDTO> updateDisponibilite(
            @RequestParam boolean disponible) {
        return ResponseEntity.ok(conducteurService.updateDisponibilite(disponible));
    }

    @GetMapping("/courses")
    @Operation(
            summary = "Historique des courses",
            description = "Récupère l'historique complet des courses effectuées par le conducteur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historique récupéré avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<CourseResponse>> getMesCourses() {
        return ResponseEntity.ok(conducteurService.getMesCourses());
    }

    @GetMapping("/statistiques")
    @Operation(
            summary = "Statistiques du conducteur",
            description = "Récupère les statistiques détaillées du conducteur (nombre de courses, gains, note moyenne, etc.)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques récupérées avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(conducteurService.getStatistiques());
    }

    @PostMapping("/position")
    @Operation(
            summary = "Mettre à jour la position GPS",
            description = "Met à jour la position géographique actuelle du conducteur en temps réel",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Coordonnées invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<Void> updatePosition(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        conducteurService.updatePosition(latitude, longitude);
        return ResponseEntity.ok().build();
    }
}