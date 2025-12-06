package com.lanayago.lanayagobackend.controller;

import com.lanayago.lanayagobackend.dto.CourseDTO;
import com.lanayago.lanayagobackend.entity.enums.StatutCourse;
import com.lanayago.lanayagobackend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDTO.CourseResponse> createCourse(
            @Valid @RequestBody CourseDTO.CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO.CourseResponse>> getMyCourses() {
        return ResponseEntity.ok(courseService.getUserCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO.CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<List<CourseDTO.CourseResponse>> getCoursesDisponibles() {
        return ResponseEntity.ok(courseService.getCoursesDisponibles());
    }

    @PutMapping("/{id}/accepter")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<CourseDTO.CourseResponse> accepterCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.accepterCourse(id));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<CourseDTO.CourseResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam StatutCourse statut) {
        return ResponseEntity.ok(courseService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> annulerCourse(@PathVariable Long id) {
        courseService.annulerCourse(id);
        return ResponseEntity.noContent().build();
    }
}