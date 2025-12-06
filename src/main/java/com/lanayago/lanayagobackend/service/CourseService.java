package com.lanayago.lanayagobackend.service;

import com.lanayago.lanayagobackend.dto.CourseDTO;
import com.lanayago.lanayagobackend.entity.*;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.entity.enums.TypeCourse;
import com.lanayago.lanayagobackend.repository.ConducteurRepository;
import com.lanayago.lanayagobackend.repository.CourseRepository;
import com.lanayago.lanayagobackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ConducteurRepository conducteurRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Transactional
    public CourseDTO.CourseResponse createCourse(CourseDTO.CourseRequest request) {
        User client = getCurrentUser();

        Course course = new Course();
        course.setTypeCourse(request.getTypeCourse());
        course.setClient(client);
        course.setAdresseDepart(request.getAdresseDepart());
        course.setLatitudeDepart(request.getLatitudeDepart());
        course.setLongitudeDepart(request.getLongitudeDepart());
        course.setAdresseArrivee(request.getAdresseArrivee());
        course.setLatitudeArrivee(request.getLatitudeArrivee());
        course.setLongitudeArrivee(request.getLongitudeArrivee());
        course.setDescriptionColis(request.getDescriptionColis());
        course.setPoidsColis(request.getPoidsColis());
        course.setNombrePassagers(request.getNombrePassagers());
        course.setNoteClient(request.getNoteClient());

        // Calculer la distance (formule simplifiée Haversine)
        double distance = calculateDistance(
                request.getLatitudeDepart(),
                request.getLongitudeDepart(),
                request.getLatitudeArrivee(),
                request.getLongitudeArrivee()
        );
        course.setDistanceKm(distance);

        // Calculer le montant estimé (tarif de base + distance)
        double tarifBase = getTarifBase(request.getTypeCourse());
        double montantEstime = tarifBase + (distance * getTarifParKm(request.getTypeCourse()));
        course.setMontantEstime(montantEstime);

        course.setStatut(StatutCourse.EN_ATTENTE);

        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    public List<CourseDTO.CourseResponse> getUserCourses() {
        User user = getCurrentUser();
        List<Course> courses = courseRepository.findByUserId(user.getId());
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public CourseDTO.CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        User currentUser = getCurrentUser();
        if (!course.getClient().getId().equals(currentUser.getId()) &&
                (course.getConducteur() == null || !course.getConducteur().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Accès non autorisé à cette course");
        }

        return convertToResponse(course);
    }

    public List<CourseDTO.CourseResponse> getCoursesDisponibles() {
        return courseRepository.findCoursesDisponibles().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO.CourseResponse accepterCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        if (course.getStatut() != StatutCourse.EN_ATTENTE) {
            throw new RuntimeException("Cette course n'est plus disponible");
        }

        User currentUser = getCurrentUser();
        if (!(currentUser instanceof Conducteur)) {
            throw new RuntimeException("Seuls les conducteurs peuvent accepter des courses");
        }

        Conducteur conducteur = (Conducteur) currentUser;
        if (conducteur.getStatut() != StatutConducteur.APPROUVE) {
            throw new RuntimeException("Votre compte conducteur n'est pas encore approuvé");
        }

        course.setConducteur(conducteur);
        course.setStatut(StatutCourse.ACCEPTEE);

        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    @Transactional
    public CourseDTO.CourseResponse updateStatut(Long courseId, StatutCourse nouveauStatut) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        User currentUser = getCurrentUser();

        // Vérifier les autorisations
        boolean isClient = course.getClient().getId().equals(currentUser.getId());
        boolean isConducteur = course.getConducteur() != null &&
                course.getConducteur().getId().equals(currentUser.getId());

        if (!isClient && !isConducteur) {
            throw new RuntimeException("Accès non autorisé");
        }

        // Gestion des transitions de statut
        if (nouveauStatut == StatutCourse.EN_COURS && course.getStatut() == StatutCourse.ARRIVEE_DEPART) {
            course.setDateDebut(LocalDateTime.now());
        } else if (nouveauStatut == StatutCourse.TERMINEE && course.getStatut() == StatutCourse.EN_COURS) {
            course.setDateFin(LocalDateTime.now());
            course.setMontantFinal(course.getMontantEstime());
        }

        course.setStatut(nouveauStatut);
        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }

    @Transactional
    public void annulerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        User currentUser = getCurrentUser();

        if (!course.getClient().getId().equals(currentUser.getId()) &&
                (course.getConducteur() == null || !course.getConducteur().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Accès non autorisé");
        }

        if (course.getStatut() == StatutCourse.EN_COURS || course.getStatut() == StatutCourse.TERMINEE) {
            throw new RuntimeException("Impossible d'annuler une course en cours ou terminée");
        }

        course.setStatut(StatutCourse.ANNULEE);
        courseRepository.save(course);
    }

    // Méthodes utilitaires

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Rayon de la Terre en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private double getTarifBase(@NotNull(message = "Le type de course est obligatoire") TypeCourse typeCourse) {
        return switch (typeCourse) {
            case LIVRAISON_MOTO -> 500.0;        // 500 FCFA
            case TRANSPORT_PERSONNE -> 1000.0;   // 1000 FCFA
            case TRANSPORT_MARCHANDISE -> 2000.0; // 2000 FCFA
        };
    }

    private double getTarifParKm(TypeCourse typeCourse) {
        return switch (typeCourse) {
            case LIVRAISON_MOTO -> 100.0;        // 100 FCFA/km
            case TRANSPORT_PERSONNE -> 150.0;    // 150 FCFA/km
            case TRANSPORT_MARCHANDISE -> 200.0;  // 200 FCFA/km
        };
    }

    private CourseDTO.CourseResponse convertToResponse(Course course) {
        CourseDTO.CourseResponse response = new CourseDTO.CourseResponse();
        response.setId(course.getId());
        response.setTypeCourse(course.getTypeCourse());
        response.setAdresseDepart(course.getAdresseDepart());
        response.setAdresseArrivee(course.getAdresseArrivee());
        response.setDistanceKm(course.getDistanceKm());
        response.setMontantEstime(course.getMontantEstime());
        response.setMontantFinal(course.getMontantFinal());
        response.setStatut(course.getStatut());
        response.setDateCreation(course.getDateCreation());
        response.setDateDebut(course.getDateDebut());
        response.setDateFin(course.getDateFin());
        response.setDescriptionColis(course.getDescriptionColis());
        response.setNombrePassagers(course.getNombrePassagers());

        // Client
        CourseDTO.UserDTO clientDTO = new CourseDTO.UserDTO();
        clientDTO.setId(course.getClient().getId());
        clientDTO.setNom(course.getClient().getNom());
        clientDTO.setPrenom(course.getClient().getPrenom());
        clientDTO.setTelephone(course.getClient().getTelephone());
        response.setClient(clientDTO);

        // Conducteur
        if (course.getConducteur() != null) {
            CourseDTO.UserDTO conducteurDTO = new CourseDTO.UserDTO();
            conducteurDTO.setId(course.getConducteur().getId());
            conducteurDTO.setNom(course.getConducteur().getNom());
            conducteurDTO.setPrenom(course.getConducteur().getPrenom());
            conducteurDTO.setTelephone(course.getConducteur().getTelephone());
            conducteurDTO.setNoteGlobale(course.getConducteur().getNoteGlobale());
            response.setConducteur(conducteurDTO);
        }

        // Engin
        if (course.getEngin() != null) {
            CourseDTO.EnginDTO enginDTO = new CourseDTO.EnginDTO();
            enginDTO.setId(course.getEngin().getId());
            enginDTO.setMarque(course.getEngin().getMarque());
            enginDTO.setModele(course.getEngin().getModele());
            enginDTO.setCouleur(course.getEngin().getCouleur());
            enginDTO.setMatricule(course.getEngin().getMatricule());
            response.setEngin(enginDTO);
        }

        return response;
    }
}