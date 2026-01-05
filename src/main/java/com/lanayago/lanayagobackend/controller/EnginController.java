package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.reponse.*;
import com.lanayago.lanayagobackend.dto.request.EnginRequest;
import com.lanayago.lanayagobackend.entity.enums.StatutEngin;
import com.lanayago.lanayagobackend.service.EnginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROPRIETAIRE')")
@Tag(name = "Engins", description = "API de gestion des véhicules (motos et voitures)")
public class EnginController {

    private final EnginService enginService;

    @PostMapping
    @Operation(
            summary = "Créer un nouvel engin",
            description = "Permet à un propriétaire d'enregistrer un nouveau véhicule (moto ou voiture)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Engin créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - réservé aux propriétaires")
    })
    public ResponseEntity<EnginResponse> createEngin(@Valid @RequestBody EnginRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enginService.createEngin(request));
    }

    @GetMapping
    @Operation(
            summary = "Lister mes engins",
            description = "Récupère la liste de tous les engins appartenant au propriétaire connecté",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<List<EnginResponse>> getMesEngins() {
        return ResponseEntity.ok(enginService.getMesEngins());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtenir un engin spécifique",
            description = "Récupère les détails d'un engin par son ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Engin trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Engin non trouvé"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<EnginResponse> getEnginById(@PathVariable Long id) {
        return ResponseEntity.ok(enginService.getEnginById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Modifier un engin",
            description = "Met à jour les informations d'un engin existant",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Engin mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Engin non trouvé"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<EnginResponse> updateEngin(
            @PathVariable Long id,
            @Valid @RequestBody EnginRequest request) {
        return ResponseEntity.ok(enginService.updateEngin(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un engin",
            description = "Désactive un engin (soft delete)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Engin supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Engin non trouvé"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<Void> deleteEngin(@PathVariable Long id) {
        enginService.deleteEngin(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{enginId}/conducteur/{conducteurId}")
    @Operation(
            summary = "Assigner un conducteur à un engin",
            description = "Associe un conducteur à un engin spécifique",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur assigné avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Engin ou conducteur non trouvé"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<EnginResponse> assignerConducteur(
            @PathVariable Long enginId,
            @PathVariable Long conducteurId) {
        return ResponseEntity.ok(enginService.assignerConducteur(enginId, conducteurId));
    }

    @PutMapping("/{id}/statut")
    @Operation(
            summary = "Changer le statut d'un engin",
            description = "Met à jour le statut d'un engin (DISPONIBLE, EN_COURSE, MAINTENANCE, INDISPONIBLE)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statut mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EnginResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Engin non trouvé"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<EnginResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutEngin statut) {
        return ResponseEntity.ok(enginService.updateStatut(id, statut));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('PROPRIETAIRE', 'ADMIN')")
    @Operation(
            summary = "Lister les engins disponibles",
            description = "Récupère la liste de tous les engins disponibles pour une course",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<List<EnginResponse>> getEnginsDisponibles() {
        return ResponseEntity.ok(enginService.getEnginsDisponibles());
    }
}