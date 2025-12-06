package com.lanayago.lanayagobackend.repository;

import com.lanayago.lanayagobackend.entity.Conducteur;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConducteurRepository extends JpaRepository<Conducteur, Long> {

    Optional<Conducteur> findByEmail(String email);

    List<Conducteur> findByStatut(StatutConducteur statut);

    boolean existsByNumPermis(String numPermis);
}