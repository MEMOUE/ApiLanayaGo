package com.lanayago.lanayagobackend.entity.enums;

public enum StatutCourse {
    EN_ATTENTE,           // Client a créé la demande
    ACCEPTEE,             // Conducteur a accepté
    EN_ROUTE_DEPART,      // Conducteur se dirige vers le point de départ
    ARRIVEE_DEPART,       // Conducteur est arrivé au point de départ
    EN_COURS,             // Course en cours
    TERMINEE,             // Course terminée
    ANNULEE               // Course annulée
}