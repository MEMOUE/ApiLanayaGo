package com.lanayago.lanayagobackend.dto.reponse;

import com.lanayago.lanayagobackend.entity.enums.StatutEngin;
import com.lanayago.lanayagobackend.entity.enums.TypeEngin;
import com.lanayago.lanayagobackend.entity.enums.TypeMoto;
import com.lanayago.lanayagobackend.entity.enums.TypeVehicule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnginResponse {
    private Long id;
    private TypeEngin typeEngin;
    private String marque;
    private String modele;
    private String couleur;
    private String matricule;
    private Integer annee;
    private String photoEngin;
    private StatutEngin statut;
    private boolean active;

    // Informations propriétaire
    private ProprietaireDTO proprietaire;

    // Spécifique aux motos
    private Integer cylindree;
    private TypeMoto typeMoto;

    // Spécifique aux voitures
    private TypeVehicule typeVehicule;
    private Integer nombrePlaces;
    private Double capaciteChargement;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProprietaireDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
    }
}