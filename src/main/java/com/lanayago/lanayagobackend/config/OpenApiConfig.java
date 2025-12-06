package com.lanayago.lanayagobackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI lanayagoOpenAPI() {
        // Définition du schéma de sécurité JWT
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Informations générales de l'API
                .info(new Info()
                        .title("Lanayago Backend API")
                        .version("1.0.0")
                        .description("""
                                API REST pour la plateforme Lanayago - Transport et livraison en Côte d'Ivoire
                                
                                ## Fonctionnalités principales
                                
                                * **🔐 Authentification** - Inscription et connexion avec JWT
                                * **🏍️ Livraison moto** - Service de livraison express
                                * **🚗 Transport de personnes** - Service de transport de passagers
                                * **🚚 Transport de marchandises** - Service de fret
                                * **👤 Gestion des utilisateurs** - Clients, Conducteurs, Propriétaires
                                * **📦 Gestion des courses** - Création, suivi, paiement
                                * **⭐ Système d'évaluation** - Notes et commentaires
                                
                                ## Authentification
                                
                                L'API utilise JWT (JSON Web Token) pour l'authentification.
                                
                                1. Inscrivez-vous via `/api/auth/register`
                                2. Connectez-vous via `/api/auth/login` pour obtenir un token
                                3. Utilisez le token dans l'en-tête `Authorization: Bearer <token>`
                                
                                ## Rôles
                                
                                * **ADMIN** - Administrateur de la plateforme
                                * **CONDUCTEUR** - Conducteur de véhicule
                                * **PROPRIETAIRE** - Propriétaire de véhicule
                                * **LIVREUR** - Client utilisant les services
                                
                                ## Statuts des courses
                                
                                * **EN_ATTENTE** - Course créée, en attente d'un conducteur
                                * **ACCEPTEE** - Course acceptée par un conducteur
                                * **EN_ROUTE_DEPART** - Conducteur en route vers le point de départ
                                * **ARRIVEE_DEPART** - Conducteur arrivé au point de départ
                                * **EN_COURS** - Course en cours
                                * **TERMINEE** - Course terminée
                                * **ANNULEE** - Course annulée
                                """)
                        .contact(new Contact()
                                .name("Support Lanayago")
                                .email("support@lanayago.ci")
                                .url("https://www.lanayago.ci"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // Serveurs
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://api.lanayago.ci")
                                .description("Serveur de production")
                ))

                // Configuration de la sécurité JWT
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre token JWT (sans le préfixe 'Bearer')")
                        ));
    }
}