package com.lanayago.lanayagobackend.service;

import com.lanayago.lanayagobackend.dto.reponse.ConducteurProfileDTO;
import com.lanayago.lanayagobackend.dto.reponse.CourseResponse;
import com.lanayago.lanayagobackend.entity.Conducteur;
import com.lanayago.lanayagobackend.entity.Course;
import com.lanayago.lanayagobackend.entity.User;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import com.lanayago.lanayagobackend.repository.ConducteurRepository;
import com.lanayago.lanayagobackend.repository.CourseRepository;
import com.lanayago.lanayagobackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConducteurService {

    private final ConducteurRepository conducteurRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private Conducteur getCurrentConducteur() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!(user instanceof Conducteur)) {
            throw new RuntimeException("L'utilisateur n'est pas un conducteur");
        }

        return (Conducteur) user;
    }

    public ConducteurProfileDTO getProfile() {
        Conducteur conducteur = getCurrentConducteur();
        return convertToProfileDTO(conducteur);
    }

    @Transactional
    public ConducteurProfileDTO updateProfile(ConducteurProfileDTO profileDTO) {
        Conducteur conducteur = getCurrentConducteur();

        conducteur.setNom(profileDTO.getNom());
        conducteur.setPrenom(profileDTO.getPrenom());
        conducteur.setTelephone(profileDTO.getTelephone());
        conducteur.setPhotoPermis(profileDTO.getPhotoPermis());

        Conducteur updatedConducteur = conducteurRepository.save(conducteur);
        return convertToProfileDTO(updatedConducteur);
    }

    @Transactional
    public ConducteurProfileDTO updateDisponibilite(boolean disponible) {
        Conducteur conducteur = getCurrentConducteur();

        if (conducteur.getStatut() != StatutConducteur.APPROUVE) {
            throw new RuntimeException("Votre compte n'est pas encore approuvé");
        }

        // Logique de disponibilité peut être gérée via le statut de l'engin
        // ou un champ dédié si nécessaire

        return convertToProfileDTO(conducteur);
    }

    public List<CourseResponse> getMesCourses() {
        Conducteur conducteur = getCurrentConducteur();
        List<Course> courses = courseRepository.findByConducteur_Id(conducteur.getId());

        return courses.stream()
                .map(this::convertToCourseResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStatistiques() {
        Conducteur conducteur = getCurrentConducteur();

        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreCoursesTotal", conducteur.getNombreCourses());
        stats.put("noteGlobale", conducteur.getNoteGlobale());
        stats.put("statut", conducteur.getStatut());

        // Statistiques sur les 30 derniers jours
        LocalDateTime debut = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        List<Course> coursesDuMois = courseRepository.findByConducteur_Id(conducteur.getId())
                .stream()
                .filter(c -> c.getDateCreation().isAfter(debut))
                .toList();

        stats.put("coursesDuMois", coursesDuMois.size());

        double gainsDuMois = coursesDuMois.stream()
                .filter(c -> c.getMontantFinal() != null)
                .mapToDouble(Course::getMontantFinal)
                .sum();

        stats.put("gainsDuMois", gainsDuMois);

        return stats;
    }

    @Transactional
    public void updatePosition(Double latitude, Double longitude) {
        Conducteur conducteur = getCurrentConducteur();
        // Logique de mise à jour de position
        // Peut être stockée dans une table dédiée ou en cache (Redis)
        // Pour l'instant, juste un log
        System.out.println("Position du conducteur " + conducteur.getId() +
                " mise à jour: " + latitude + ", " + longitude);
    }

    private ConducteurProfileDTO convertToProfileDTO(Conducteur conducteur) {
        ConducteurProfileDTO dto = new ConducteurProfileDTO();
        dto.setId(conducteur.getId());
        dto.setNom(conducteur.getNom());
        dto.setPrenom(conducteur.getPrenom());
        dto.setEmail(conducteur.getEmail());
        dto.setTelephone(conducteur.getTelephone());
        dto.setRole(conducteur.getRole());
        dto.setNumPermis(conducteur.getNumPermis());
        dto.setPhotoPermis(conducteur.getPhotoPermis());
        dto.setStatut(conducteur.getStatut());
        dto.setNoteGlobale(conducteur.getNoteGlobale());
        dto.setNombreCourses(conducteur.getNombreCourses());

        if (conducteur.getEnginActuel() != null) {
            ConducteurProfileDTO.EnginSimpleDTO enginDTO = new ConducteurProfileDTO.EnginSimpleDTO();
            enginDTO.setId(conducteur.getEnginActuel().getId());
            enginDTO.setMarque(conducteur.getEnginActuel().getMarque());
            enginDTO.setModele(conducteur.getEnginActuel().getModele());
            enginDTO.setCouleur(conducteur.getEnginActuel().getCouleur());
            enginDTO.setMatricule(conducteur.getEnginActuel().getMatricule());
            dto.setEnginActuel(enginDTO);
        }

        return dto;
    }

    private CourseResponse convertToCourseResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTypeCourse(course.getTypeCourse());
        response.setAdresseDepart(course.getAdresseDepart());
        response.setAdresseArrivee(course.getAdresseArrivee());
        response.setDistanceKm(course.getDistanceKm());
        response.setMontantEstime(course.getMontantEstime());
        response.setMontantFinal(course.getMontantFinal());
        response.setStatut(course.getStatut());
        response.setDateCreation(course.getDateCreation().toString());

        return response;
    }
}