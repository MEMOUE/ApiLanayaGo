package com.lanayago.lanayagobackend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private Double noteGlobale;
}
