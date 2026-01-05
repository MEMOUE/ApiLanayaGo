package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.reponse.AuthResponse;
import com.lanayago.lanayagobackend.dto.request.LoginRequest;
import com.lanayago.lanayagobackend.dto.request.RegisterRequest;
import com.lanayago.lanayagobackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API d'authentification et gestion des utilisateurs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Inscription d'un nouvel utilisateur",
            description = """
                    Permet de créer un nouveau compte utilisateur. Supporte plusieurs types d'utilisateurs:
                    - CLIENT (client standard)
                    - LIVREUR 
                    - CONDUCTEUR (nécessite un numéro de permis)
                    - PROPRIETAIRE (propriétaire de véhicules)
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations d'inscription",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Client standard",
                                            value = """
                                                    {
                                                      "nom": "Kouame",
                                                      "prenom": "Jean",
                                                      "email": "jean.kouame@example.com",
                                                      "password": "Password123!",
                                                      "telephone": "+2250700000001",
                                                      "role": "LIVREUR"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Conducteur",
                                            value = """
                                                    {
                                                      "nom": "Kone",
                                                      "prenom": "Ibrahim",
                                                      "email": "ibrahim.kone@example.com",
                                                      "password": "Password123!",
                                                      "telephone": "+2250700000002",
                                                      "role": "CONDUCTEUR",
                                                      "numPermis": "CI123456789"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Propriétaire",
                                            value = """
                                                    {
                                                      "nom": "Bakayoko",
                                                      "prenom": "Fatou",
                                                      "email": "fatou.bakayoko@example.com",
                                                      "password": "Password123!",
                                                      "telephone": "+2250700000003",
                                                      "role": "PROPRIETAIRE"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Inscription réussie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides (email déjà utilisé, téléphone déjà utilisé, etc.)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Connexion d'un utilisateur",
            description = "Authentifie un utilisateur et retourne un token JWT valide pour 24 heures",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants de connexion",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemple de connexion",
                                    value = """
                                            {
                                              "email": "jean.kouame@example.com",
                                              "password": "Password123!"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Réponse de connexion",
                                    value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                              "id": 1,
                                              "email": "jean.kouame@example.com",
                                              "nom": "Kouame",
                                              "prenom": "Jean",
                                              "telephone": "+2250700000001",
                                              "role": "LIVREUR"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Email ou mot de passe incorrect",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Récupérer les informations de l'utilisateur connecté",
            description = "Retourne les informations complètes de l'utilisateur actuellement authentifié",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informations récupérées avec succès",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non authentifié (token manquant ou invalide)",
                    content = @Content(mediaType = "application/json")
            )
    })
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}