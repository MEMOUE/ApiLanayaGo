package com.lanayago.lanayagobackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class user {
    @Id
    private Long id;

    private String nom;
    private String prenom;
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;


}
