package com.lanayago.lanayagobackend.repository;

import com.lanayago.lanayagobackend.entity.Engin;
import com.lanayago.lanayagobackend.entity.Propriotaire;
import com.lanayago.lanayagobackend.entity.enums.StatutEngin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnginRepository extends JpaRepository<Engin, Long> {

    List<Engin> findByPropriotaire(Propriotaire proprietaire);

    List<Engin> findByStatut(StatutEngin statut);

    boolean existsByMatricule(String matricule);
}