package com.lanayago.lanayagobackend.entity.enums;

public enum StatutConducteur {
    EN_ATTENTE,     // En attente de validation
    APPROUVE,       // Approuvé et peut travailler
    SUSPENDU,       // Suspendu temporairement
    BLOQUE          // Bloqué définitivement
}