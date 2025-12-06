package com.lanayago.lanayagobackend.repository;

import com.lanayago.lanayagobackend.entity.Course;
import com.lanayago.lanayagobackend.entity.enums.   StatutCourse;
import com.lanayago.lanayagobackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByClient(User client);

    List<Course> findByConducteur_Id(Long conducteurId);

    List<Course> findByStatut(StatutCourse statut);

    @Query("SELECT c FROM Course c WHERE c.statut = 'EN_ATTENTE' ORDER BY c.dateCreation ASC")
    List<Course> findCoursesDisponibles();

    @Query("SELECT c FROM Course c WHERE c.client.id = :userId OR c.conducteur.id = :userId ORDER BY c.dateCreation DESC")
    List<Course> findByUserId(Long userId);
}