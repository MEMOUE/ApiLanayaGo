package com.lanayago.lanayagobackend.service;

import com.lanayago.lanayagobackend.dto.reponse.*;
import com.lanayago.lanayagobackend.dto.request.EnginRequest;
import com.lanayago.lanayagobackend.entity.*;
import com.lanayago.lanayagobackend.entity.enums.StatutEngin;
import com.lanayago.lanayagobackend.entity.enums.TypeEngin;
import com.lanayago.lanayagobackend.repository.ConducteurRepository;
import com.lanayago.lanayagobackend.repository.EnginRepository;
import com.lanayago.lanayagobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnginService {

    private final EnginRepository enginRepository;
    private final UserRepository userRepository;
    private final ConducteurRepository conducteurRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public EnginResponse createEngin(EnginRequest request) {
        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire)) {
            throw new RuntimeException("Seuls les propriétaires peuvent créer des engins");
        }

        Propriotaire proprietaire = (Propriotaire) currentUser;

        // Vérifier si le matricule existe déjà
        if (enginRepository.existsByMatricule(request.getMatricule())) {
            throw new RuntimeException("Un engin avec ce matricule existe déjà");
        }

        Engin engin;

        // Créer le bon type d'engin
        if (request.getTypeEngin() == TypeEngin.MOTO) {
            Moto moto = new Moto();
            moto.setCylindree(request.getCylindree());
            moto.setTypeMoto(request.getTypeMoto());
            engin = moto;
        } else if (request.getTypeEngin() == TypeEngin.VOITURE) {
            Voiture voiture = new Voiture();
            voiture.setTypeVehicule(request.getTypeVehicule());
            voiture.setNombrePlaces(request.getNombrePlaces());
            voiture.setCapaciteChargement(request.getCapaciteChargement());
            engin = voiture;
        } else {
            throw new RuntimeException("Type d'engin non supporté");
        }

        // Définir les propriétés communes
        engin.setMarque(request.getMarque());
        engin.setModele(request.getModele());
        engin.setCouleur(request.getCouleur());
        engin.setMatricule(request.getMatricule());
        engin.setAnnee(request.getAnnee());
        engin.setPhotoEngin(request.getPhotoEngin());
        engin.setPropriotaire(proprietaire);
        engin.setStatut(StatutEngin.DISPONIBLE);
        engin.setActive(true);

        Engin savedEngin = enginRepository.save(engin);
        return convertToResponse(savedEngin);
    }

    public List<EnginResponse> getMesEngins() {
        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire)) {
            throw new RuntimeException("Seuls les propriétaires peuvent consulter leurs engins");
        }

        Propriotaire proprietaire = (Propriotaire) currentUser;
        List<Engin> engins = enginRepository.findByPropriotaire(proprietaire);

        return engins.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EnginResponse getEnginById(Long id) {
        Engin engin = enginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engin non trouvé"));

        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire) ||
                !engin.getPropriotaire().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès non autorisé à cet engin");
        }

        return convertToResponse(engin);
    }

    @Transactional
    public EnginResponse updateEngin(Long id, EnginRequest request) {
        Engin engin = enginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engin non trouvé"));

        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire) ||
                !engin.getPropriotaire().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès non autorisé à cet engin");
        }

        // Vérifier si le nouveau matricule existe déjà
        if (!engin.getMatricule().equals(request.getMatricule()) &&
                enginRepository.existsByMatricule(request.getMatricule())) {
            throw new RuntimeException("Un engin avec ce matricule existe déjà");
        }

        // Mettre à jour les propriétés communes
        engin.setMarque(request.getMarque());
        engin.setModele(request.getModele());
        engin.setCouleur(request.getCouleur());
        engin.setMatricule(request.getMatricule());
        engin.setAnnee(request.getAnnee());
        engin.setPhotoEngin(request.getPhotoEngin());

        // Mettre à jour les propriétés spécifiques
        if (engin instanceof Moto moto) {
            moto.setCylindree(request.getCylindree());
            moto.setTypeMoto(request.getTypeMoto());
        } else if (engin instanceof Voiture voiture) {
            voiture.setTypeVehicule(request.getTypeVehicule());
            voiture.setNombrePlaces(request.getNombrePlaces());
            voiture.setCapaciteChargement(request.getCapaciteChargement());
        }

        Engin updatedEngin = enginRepository.save(engin);
        return convertToResponse(updatedEngin);
    }

    @Transactional
    public void deleteEngin(Long id) {
        Engin engin = enginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engin non trouvé"));

        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire) ||
                !engin.getPropriotaire().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès non autorisé à cet engin");
        }

        // Soft delete
        engin.setActive(false);
        engin.setStatut(StatutEngin.INDISPONIBLE);
        enginRepository.save(engin);
    }

    @Transactional
    public EnginResponse assignerConducteur(Long enginId, Long conducteurId) {
        Engin engin = enginRepository.findById(enginId)
                .orElseThrow(() -> new RuntimeException("Engin non trouvé"));

        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire) ||
                !engin.getPropriotaire().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès non autorisé à cet engin");
        }

        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        // Assigner l'engin au conducteur
        conducteur.setEnginActuel(engin);
        conducteurRepository.save(conducteur);

        return convertToResponse(engin);
    }

    @Transactional
    public EnginResponse updateStatut(Long id, StatutEngin nouveauStatut) {
        Engin engin = enginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engin non trouvé"));

        User currentUser = getCurrentUser();

        if (!(currentUser instanceof Propriotaire) ||
                !engin.getPropriotaire().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Accès non autorisé à cet engin");
        }

        engin.setStatut(nouveauStatut);
        Engin updatedEngin = enginRepository.save(engin);

        return convertToResponse(updatedEngin);
    }

    public List<EnginResponse> getEnginsDisponibles() {
        List<Engin> engins = enginRepository.findByStatut(StatutEngin.DISPONIBLE);

        return engins.stream()
                .filter(Engin::isActive)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private EnginResponse convertToResponse(Engin engin) {
        EnginResponse response = new EnginResponse();
        response.setId(engin.getId());
        response.setMarque(engin.getMarque());
        response.setModele(engin.getModele());
        response.setCouleur(engin.getCouleur());
        response.setMatricule(engin.getMatricule());
        response.setAnnee(engin.getAnnee());
        response.setPhotoEngin(engin.getPhotoEngin());
        response.setStatut(engin.getStatut());
        response.setActive(engin.isActive());

        // Propriétaire
        EnginResponse.ProprietaireDTO proprietaireDTO = new EnginResponse.ProprietaireDTO();
        proprietaireDTO.setId(engin.getPropriotaire().getId());
        proprietaireDTO.setNom(engin.getPropriotaire().getNom());
        proprietaireDTO.setPrenom(engin.getPropriotaire().getPrenom());
        proprietaireDTO.setTelephone(engin.getPropriotaire().getTelephone());
        response.setProprietaire(proprietaireDTO);

        // Type d'engin
        if (engin instanceof Moto moto) {
            response.setTypeEngin(TypeEngin.MOTO);
            response.setCylindree(moto.getCylindree());
            response.setTypeMoto(moto.getTypeMoto());
        } else if (engin instanceof Voiture voiture) {
            response.setTypeEngin(TypeEngin.VOITURE);
            response.setTypeVehicule(voiture.getTypeVehicule());
            response.setNombrePlaces(voiture.getNombrePlaces());
            response.setCapaciteChargement(voiture.getCapaciteChargement());
        }

        return response;
    }
}