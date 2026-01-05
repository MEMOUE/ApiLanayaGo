package com.lanayago.lanayagobackend.service;

import com.lanayago.lanayagobackend.dto.reponse.AdminDashboardDTO;
import com.lanayago.lanayagobackend.dto.reponse.ConducteurProfileDTO;
import com.lanayago.lanayagobackend.dto.reponse.CourseResponse;
import com.lanayago.lanayagobackend.dto.reponse.EnginResponse;
import com.lanayago.lanayagobackend.entity.*;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.repository.*;
import lombok.RequiredArgsConstructor;
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
public class AdminService {

    private final UserRepository userRepository;
    private final ConducteurRepository conducteurRepository;
    private final CourseRepository courseRepository;
    private final EnginRepository enginRepository;

    /**
     * Tableau de bord administrateur avec statistiques globales
     */
    public AdminDashboardDTO getDashboard() {
        AdminDashboardDTO dashboard = new AdminDashboardDTO();

        // Statistiques utilisateurs
        long totalUsers = userRepository.count();
        long totalConducteurs = conducteurRepository.count();
        long conducteursApprouves = conducteurRepository.findByStatut(StatutConducteur.APPROUVE).size();
        long conducteursEnAttente = conducteurRepository.findByStatut(StatutConducteur.EN_ATTENTE).size();

        dashboard.setTotalUtilisateurs(totalUsers);
        dashboard.setTotalConducteurs(totalConducteurs);
        dashboard.setConducteursApprouves(conducteursApprouves);
        dashboard.setConducteursEnAttente(conducteursEnAttente);

        // Statistiques courses
        long totalCourses = courseRepository.count();
        long coursesEnAttente = courseRepository.findByStatut(StatutCourse.EN_ATTENTE).size();
        long coursesEnCours = courseRepository.findByStatut(StatutCourse.EN_COURS).size();
        long coursesTerminees = courseRepository.findByStatut(StatutCourse.TERMINEE).size();

        dashboard.setTotalCourses(totalCourses);
        dashboard.setCoursesEnAttente(coursesEnAttente);
        dashboard.setCoursesEnCours(coursesEnCours);
        dashboard.setCoursesTerminees(coursesTerminees);

        // Statistiques engins
        long totalEngins = enginRepository.count();
        dashboard.setTotalEngins(totalEngins);

        // Revenus du mois
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        double revenusMois = courseRepository.findAll().stream()
                .filter(c -> c.getDateFin() != null && c.getDateFin().isAfter(debutMois))
                .filter(c -> c.getMontantFinal() != null)
                .mapToDouble(Course::getMontantFinal)
                .sum();

        dashboard.setRevenusMois(revenusMois);

        // Évolution des courses (30 derniers jours)
        LocalDateTime debutPeriode = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        long coursesRecentes = courseRepository.findAll().stream()
                .filter(c -> c.getDateCreation().isAfter(debutPeriode))
                .count();

        dashboard.setCoursesRecentes(coursesRecentes);

        return dashboard;
    }

    /**
     * Gestion des conducteurs - Liste de tous les conducteurs
     */
    public List<ConducteurProfileDTO> getAllConducteurs() {
        return conducteurRepository.findAll().stream()
                .map(this::convertConducteurToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gestion des conducteurs - Conducteurs en attente d'approbation
     */
    public List<ConducteurProfileDTO> getConducteursEnAttente() {
        return conducteurRepository.findByStatut(StatutConducteur.EN_ATTENTE).stream()
                .map(this::convertConducteurToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gestion des conducteurs - Détails d'un conducteur
     */
    public ConducteurProfileDTO getConducteurById(Long id) {
        Conducteur conducteur = conducteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));
        return convertConducteurToDTO(conducteur);
    }

    /**
     * Approuver un conducteur
     */
    @Transactional
    public ConducteurProfileDTO approuverConducteur(Long conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        if (conducteur.getStatut() == StatutConducteur.APPROUVE) {
            throw new RuntimeException("Ce conducteur est déjà approuvé");
        }

        conducteur.setStatut(StatutConducteur.APPROUVE);
        Conducteur updated = conducteurRepository.save(conducteur);

        // TODO: Envoyer notification au conducteur
        return convertConducteurToDTO(updated);
    }

    /**
     * Suspendre un conducteur
     */
    @Transactional
    public ConducteurProfileDTO suspendreConducteur(Long conducteurId, String motif) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        conducteur.setStatut(StatutConducteur.SUSPENDU);
        Conducteur updated = conducteurRepository.save(conducteur);

        // TODO: Envoyer notification au conducteur avec le motif
        return convertConducteurToDTO(updated);
    }

    /**
     * Bloquer définitivement un conducteur
     */
    @Transactional
    public ConducteurProfileDTO bloquerConducteur(Long conducteurId, String motif) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        conducteur.setStatut(StatutConducteur.BLOQUE);
        conducteur.setActif(false);
        Conducteur updated = conducteurRepository.save(conducteur);

        // TODO: Envoyer notification au conducteur avec le motif
        return convertConducteurToDTO(updated);
    }

    /**
     * Réactiver un conducteur suspendu
     */
    @Transactional
    public ConducteurProfileDTO reactiverConducteur(Long conducteurId) {
        Conducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        if (conducteur.getStatut() == StatutConducteur.BLOQUE) {
            throw new RuntimeException("Un conducteur bloqué ne peut pas être réactivé automatiquement");
        }

        conducteur.setStatut(StatutConducteur.APPROUVE);
        conducteur.setActif(true);
        Conducteur updated = conducteurRepository.save(conducteur);

        return convertConducteurToDTO(updated);
    }

    /**
     * Gestion des courses - Liste de toutes les courses
     */
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gestion des courses - Courses par statut
     */
    public List<CourseResponse> getCoursesByStatut(StatutCourse statut) {
        return courseRepository.findByStatut(statut).stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gestion des courses - Détails d'une course
     */
    public Map<String, Object> getCourseDetails(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        Map<String, Object> details = new HashMap<>();
        details.put("course", convertCourseToDTO(course));
        details.put("client", convertUserToDTO(course.getClient()));

        if (course.getConducteur() != null) {
            details.put("conducteur", convertConducteurToDTO(course.getConducteur()));
        }

        if (course.getEngin() != null) {
            details.put("engin", convertEnginToDTO(course.getEngin()));
        }

        if (course.getEvaluation() != null) {
            details.put("evaluation", course.getEvaluation());
        }

        return details;
    }

    /**
     * Annuler une course (admin)
     */
    @Transactional
    public void annulerCourseAdmin(Long courseId, String motif) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course non trouvée"));

        if (course.getStatut() == StatutCourse.TERMINEE) {
            throw new RuntimeException("Impossible d'annuler une course terminée");
        }

        course.setStatut(StatutCourse.ANNULEE);
        course.setNoteClient(motif);
        courseRepository.save(course);

        // TODO: Notifier le client et le conducteur
    }

    /**
     * Statistiques détaillées
     */
    public Map<String, Object> getStatistiquesDetaillees() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques par type de course
        Map<String, Long> coursesByType = new HashMap<>();
        coursesByType.put("LIVRAISON_MOTO", courseRepository.findAll().stream()
                .filter(c -> c.getTypeCourse().name().equals("LIVRAISON_MOTO"))
                .count());
        coursesByType.put("TRANSPORT_PERSONNE", courseRepository.findAll().stream()
                .filter(c -> c.getTypeCourse().name().equals("TRANSPORT_PERSONNE"))
                .count());
        coursesByType.put("TRANSPORT_MARCHANDISE", courseRepository.findAll().stream()
                .filter(c -> c.getTypeCourse().name().equals("TRANSPORT_MARCHANDISE"))
                .count());

        stats.put("coursesParType", coursesByType);

        // Top 10 conducteurs
        List<Map<String, Object>> topConducteurs = conducteurRepository.findAll().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getNombreCourses(), c1.getNombreCourses()))
                .limit(10)
                .map(c -> {
                    Map<String, Object> conducteurStats = new HashMap<>();
                    conducteurStats.put("id", c.getId());
                    conducteurStats.put("nom", c.getNom() + " " + c.getPrenom());
                    conducteurStats.put("nombreCourses", c.getNombreCourses());
                    conducteurStats.put("noteGlobale", c.getNoteGlobale());
                    return conducteurStats;
                })
                .collect(Collectors.toList());

        stats.put("topConducteurs", topConducteurs);

        // Revenus par période
        LocalDateTime debutSemaine = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);

        double revenusSemaine = courseRepository.findAll().stream()
                .filter(c -> c.getDateFin() != null && c.getDateFin().isAfter(debutSemaine))
                .filter(c -> c.getMontantFinal() != null)
                .mapToDouble(Course::getMontantFinal)
                .sum();

        double revenusMois = courseRepository.findAll().stream()
                .filter(c -> c.getDateFin() != null && c.getDateFin().isAfter(debutMois))
                .filter(c -> c.getMontantFinal() != null)
                .mapToDouble(Course::getMontantFinal)
                .sum();

        Map<String, Double> revenus = new HashMap<>();
        revenus.put("semaine", revenusSemaine);
        revenus.put("mois", revenusMois);

        stats.put("revenus", revenus);

        // Taux de complétion des courses
        long totalCourses = courseRepository.count();
        long coursesTerminees = courseRepository.findByStatut(StatutCourse.TERMINEE).size();
        long coursesAnnulees = courseRepository.findByStatut(StatutCourse.ANNULEE).size();

        double tauxCompletion = totalCourses > 0 ? (coursesTerminees * 100.0 / totalCourses) : 0;
        double tauxAnnulation = totalCourses > 0 ? (coursesAnnulees * 100.0 / totalCourses) : 0;

        Map<String, Double> taux = new HashMap<>();
        taux.put("completion", tauxCompletion);
        taux.put("annulation", tauxAnnulation);

        stats.put("taux", taux);

        return stats;
    }

    /**
     * Gestion des utilisateurs - Liste de tous les utilisateurs
     */
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Désactiver un utilisateur
     */
    @Transactional
    public void desactiverUtilisateur(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setActif(false);
        userRepository.save(user);
    }

    /**
     * Réactiver un utilisateur
     */
    @Transactional
    public void reactiverUtilisateur(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        user.setActif(true);
        userRepository.save(user);
    }

    /**
     * Gestion des engins - Liste de tous les engins
     */
    public List<EnginResponse> getAllEngins() {
        return enginRepository.findAll().stream()
                .map(this::convertEnginToDTO)
                .collect(Collectors.toList());
    }

    // ==================== Méthodes de conversion ====================

    private ConducteurProfileDTO convertConducteurToDTO(Conducteur conducteur) {
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

    private CourseResponse convertCourseToDTO(Course course) {
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

    private Map<String, Object> convertUserToDTO(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("nom", user.getNom());
        userMap.put("prenom", user.getPrenom());
        userMap.put("email", user.getEmail());
        userMap.put("telephone", user.getTelephone());
        userMap.put("role", user.getRole());
        userMap.put("actif", user.isActif());
        userMap.put("createdAt", user.getCreatedAt());

        return userMap;
    }

    private EnginResponse convertEnginToDTO(Engin engin) {
        EnginResponse response = new EnginResponse();
        response.setId(engin.getId());
        response.setMarque(engin.getMarque());
        response.setModele(engin.getModele());
        response.setCouleur(engin.getCouleur());
        response.setMatricule(engin.getMatricule());
        response.setAnnee(engin.getAnnee());
        response.setStatut(engin.getStatut());
        response.setActive(engin.isActive());

        EnginResponse.ProprietaireDTO proprietaireDTO = new EnginResponse.ProprietaireDTO();
        proprietaireDTO.setId(engin.getPropriotaire().getId());
        proprietaireDTO.setNom(engin.getPropriotaire().getNom());
        proprietaireDTO.setPrenom(engin.getPropriotaire().getPrenom());
        proprietaireDTO.setTelephone(engin.getPropriotaire().getTelephone());
        response.setProprietaire(proprietaireDTO);

        return response;
    }
}