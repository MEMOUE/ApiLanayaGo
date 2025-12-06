package com.lanayago.lanayagobackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    public void notifierNouvelleCourse(Long conducteurId, Long courseId) {
        log.info("Notification envoyée au conducteur {} pour la course {}", conducteurId, courseId);
        // TODO: Implémenter l'envoi de notifications (FCM, SMS, Email, etc.)
    }

    public void notifierCourseAcceptee(Long clientId, Long courseId) {
        log.info("Notification envoyée au client {} - course {} acceptée", clientId, courseId);
        // TODO: Implémenter l'envoi de notifications
    }

    public void notifierArriveeDepart(Long clientId, Long courseId) {
        log.info("Notification envoyée au client {} - conducteur arrivé pour la course {}", clientId, courseId);
        // TODO: Implémenter l'envoi de notifications
    }

    public void notifierCourseTerminee(Long clientId, Long courseId) {
        log.info("Notification envoyée au client {} - course {} terminée", clientId, courseId);
        // TODO: Implémenter l'envoi de notifications
    }
}