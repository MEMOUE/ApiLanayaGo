package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.CourseDTO;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.service.CourseService;
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
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "API de gestion des courses (livraison et transport)")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(
            summary = "Créer une nouvelle course",
            description = "Permet à un client de créer une demande de course (livraison, transport de personnes ou marchandises)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Course créée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.CourseResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<CourseDTO.CourseResponse> createCourse(
            @Valid @RequestBody CourseDTO.CourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(request));
    }

    @GetMapping
    @Operation(
            summary = "Mes courses",
            description = "Récupère la liste de toutes les courses de l'utilisateur connecté (client ou conducteur)",
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
    public ResponseEntity<List<CourseDTO.CourseResponse>> getMyCourses() {
        return ResponseEntity.ok(courseService.getUserCourses());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Détails d'une course",
            description = "Récupère les détails complets d'une course spécifique",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course trouvée",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.CourseResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Course non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<CourseDTO.CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    @Operation(
            summary = "Courses disponibles",
            description = "Liste toutes les courses en attente d'un conducteur",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste récupérée avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Réservé aux conducteurs")
    })
    public ResponseEntity<List<CourseDTO.CourseResponse>> getCoursesDisponibles() {
        return ResponseEntity.ok(courseService.getCoursesDisponibles());
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    @Operation(
            summary = "Accepter une course",
            description = "Permet à un conducteur d'accepter une course disponible",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Course acceptée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.CourseResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Course non trouvée"),
            @ApiResponse(responseCode = "400", description = "Course déjà acceptée"),
            @ApiResponse(responseCode = "403", description = "Conducteur non approuvé")
    })
    public ResponseEntity<CourseDTO.CourseResponse> accepterCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.accepterCourse(id));
    }

    @PutMapping("/{id}/statut")
    @Operation(
            summary = "Mettre à jour le statut d'une course",
            description = "Change le statut d'une course (EN_ROUTE_DEPART, ARRIVEE_DEPART, EN_COURS, TERMINEE, etc.)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statut mis à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourseDTO.CourseResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Course non trouvée"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<CourseDTO.CourseResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutCourse statut) {
        return ResponseEntity.ok(courseService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Annuler une course",
            description = "Permet d'annuler une course (uniquement si elle n'est pas encore en cours ou terminée)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course annulée avec succès"),
            @ApiResponse(responseCode = "404", description = "Course non trouvée"),
            @ApiResponse(responseCode = "400", description = "Impossible d'annuler la course"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<Void> annulerCourse(@PathVariable Long id) {
        courseService.annulerCourse(id);
        return ResponseEntity.noContent().build();
    }
}