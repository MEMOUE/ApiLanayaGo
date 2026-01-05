package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.reponse.AdminDashboardDTO;
import com.lanayago.lanayagobackend.dto.reponse.ConducteurProfileDTO;
import com.lanayago.lanayagobackend.dto.reponse.CourseResponse;
import com.lanayago.lanayagobackend.dto.reponse.EnginResponse;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "API de gestion administrative de la plateforme")
public class AdminController {

    private final AdminService adminService;

    // ==================== DASHBOARD & STATISTIQUES ====================

    @GetMapping("/dashboard")
    @Operation(
            summary = "Tableau de bord administrateur",
            description = "Récupère les statistiques globales de la plateforme pour le tableau de bord admin",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques récupérées avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminDashboardDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - réservé aux administrateurs")
    })
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/statistiques")
    @Operation(
            summary = "Statistiques détaillées",
            description = "Récupère des statistiques détaillées incluant les revenus, top conducteurs, taux de complétion, etc.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistiques détaillées récupérées avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<Map<String, Object>> getStatistiquesDetaillees() {
        return ResponseEntity.ok(adminService.getStatistiquesDetaillees());
    }

    // ==================== GESTION DES CONDUCTEURS ====================

    @GetMapping("/conducteurs")
    @Operation(
            summary = "Liste de tous les conducteurs",
            description = "Récupère la liste complète de tous les conducteurs inscrits sur la plateforme",
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
    public ResponseEntity<List<ConducteurProfileDTO>> getAllConducteurs() {
        return ResponseEntity.ok(adminService.getAllConducteurs());
    }

    @GetMapping("/conducteurs/en-attente")
    @Operation(
            summary = "Conducteurs en attente d'approbation",
            description = "Récupère la liste des conducteurs qui attendent la validation de leur compte",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<ConducteurProfileDTO>> getConducteursEnAttente() {
        return ResponseEntity.ok(adminService.getConducteursEnAttente());
    }

    @GetMapping("/conducteurs/{id}")
    @Operation(
            summary = "Détails d'un conducteur",
            description = "Récupère les informations détaillées d'un conducteur spécifique",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur trouvé",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé")
    })
    public ResponseEntity<ConducteurProfileDTO> getConducteurById(
            @Parameter(description = "ID du conducteur") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getConducteurById(id));
    }

    @PutMapping("/conducteurs/{id}/approuver")
    @Operation(
            summary = "Approuver un conducteur",
            description = "Approuve le compte d'un conducteur et l'autorise à accepter des courses",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur approuvé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Conducteur déjà approuvé")
    })
    public ResponseEntity<ConducteurProfileDTO> approuverConducteur(
            @Parameter(description = "ID du conducteur") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.approuverConducteur(id));
    }

    @PutMapping("/conducteurs/{id}/suspendre")
    @Operation(
            summary = "Suspendre un conducteur",
            description = "Suspend temporairement le compte d'un conducteur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur suspendu avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé")
    })
    public ResponseEntity<ConducteurProfileDTO> suspendreConducteur(
            @Parameter(description = "ID du conducteur") @PathVariable Long id,
            @Parameter(description = "Motif de la suspension") @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(adminService.suspendreConducteur(id, motif));
    }

    @PutMapping("/conducteurs/{id}/bloquer")
    @Operation(
            summary = "Bloquer un conducteur",
            description = "Bloque définitivement le compte d'un conducteur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur bloqué avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé")
    })
    public ResponseEntity<ConducteurProfileDTO> bloquerConducteur(
            @Parameter(description = "ID du conducteur") @PathVariable Long id,
            @Parameter(description = "Motif du blocage") @RequestParam(required = false) String motif) {
        return ResponseEntity.ok(adminService.bloquerConducteur(id, motif));
    }

    @PutMapping("/conducteurs/{id}/reactiver")
    @Operation(
            summary = "Réactiver un conducteur",
            description = "Réactive le compte d'un conducteur suspendu",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Conducteur réactivé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConducteurProfileDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Conducteur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Conducteur bloqué ne peut pas être réactivé")
    })
    public ResponseEntity<ConducteurProfileDTO> reactiverConducteur(
            @Parameter(description = "ID du conducteur") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.reactiverConducteur(id));
    }

    // ==================== GESTION DES COURSES ====================

    @GetMapping("/courses")
    @Operation(
            summary = "Liste de toutes les courses",
            description = "Récupère la liste complète de toutes les courses sur la plateforme",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(adminService.getAllCourses());
    }

    @GetMapping("/courses/statut/{statut}")
    @Operation(
            summary = "Courses par statut",
            description = "Récupère la liste des courses filtrées par statut",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<CourseResponse>> getCoursesByStatut(
            @Parameter(description = "Statut de la course") @PathVariable StatutCourse statut) {
        return ResponseEntity.ok(adminService.getCoursesByStatut(statut));
    }

    @GetMapping("/courses/{id}")
    @Operation(
            summary = "Détails complets d'une course",
            description = "Récupère tous les détails d'une course incluant client, conducteur, engin et évaluation",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Détails récupérés avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "Course non trouvée")
    })
    public ResponseEntity<Map<String, Object>> getCourseDetails(
            @Parameter(description = "ID de la course") @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCourseDetails(id));
    }

    @DeleteMapping("/courses/{id}/annuler")
    @Operation(
            summary = "Annuler une course (Admin)",
            description = "Permet à un administrateur d'annuler une course avec un motif",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course annulée avec succès"),
            @ApiResponse(responseCode = "404", description = "Course non trouvée"),
            @ApiResponse(responseCode = "400", description = "Course terminée ne peut pas être annulée")
    })
    public ResponseEntity<Void> annulerCourse(
            @Parameter(description = "ID de la course") @PathVariable Long id,
            @Parameter(description = "Motif de l'annulation") @RequestParam(required = false) String motif) {
        adminService.annulerCourseAdmin(id, motif);
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES UTILISATEURS ====================

    @GetMapping("/utilisateurs")
    @Operation(
            summary = "Liste de tous les utilisateurs",
            description = "Récupère la liste complète de tous les utilisateurs de la plateforme",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/utilisateurs/{id}/desactiver")
    @Operation(
            summary = "Désactiver un utilisateur",
            description = "Désactive le compte d'un utilisateur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur désactivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> desactiverUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        adminService.desactiverUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/utilisateurs/{id}/reactiver")
    @Operation(
            summary = "Réactiver un utilisateur",
            description = "Réactive le compte d'un utilisateur désactivé",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur réactivé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Void> reactiverUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        adminService.reactiverUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES ENGINS ====================

    @GetMapping("/engins")
    @Operation(
            summary = "Liste de tous les engins",
            description = "Récupère la liste complète de tous les engins enregistrés sur la plateforme",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<EnginResponse>> getAllEngins() {
        return ResponseEntity.ok(adminService.getAllEngins());
    }
}