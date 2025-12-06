package com.lanayago.lanayagobackend.service;

import com.lanayago.lanayagobackend.dto.reponse.AuthResponse;
import com.lanayago.lanayagobackend.dto.request.LoginRequest;
import com.lanayago.lanayagobackend.dto.request.RegisterRequest;
import com.lanayago.lanayagobackend.entity.*;
import com.lanayago.lanayagobackend.entity.enums.Roles;
import com.lanayago.lanayagobackend.entity.enums.StatutConducteur;
import com.lanayago.lanayagobackend.repository.ConducteurRepository;
import com.lanayago.lanayagobackend.repository.UserRepository;
import com.lanayago.lanayagobackend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ConducteurRepository conducteurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        if (userRepository.existsByTelephone(request.getTelephone())) {
            throw new RuntimeException("Un utilisateur avec ce numéro de téléphone existe déjà");
        }

        User user;

        // Créer l'utilisateur selon le rôle
        if (request.getRole() == Roles.CONDUCTEUR) {
            if (request.getNumPermis() == null || request.getNumPermis().isEmpty()) {
                throw new RuntimeException("Le numéro de permis est obligatoire pour un conducteur");
            }

            if (conducteurRepository.existsByNumPermis(request.getNumPermis())) {
                throw new RuntimeException("Un conducteur avec ce numéro de permis existe déjà");
            }

            Conducteur conducteur = new Conducteur();
            conducteur.setNom(request.getNom());
            conducteur.setPrenom(request.getPrenom());
            conducteur.setEmail(request.getEmail());
            conducteur.setPassword(passwordEncoder.encode(request.getPassword()));
            conducteur.setTelephone(request.getTelephone());
            conducteur.setRole(Roles.CONDUCTEUR);
            conducteur.setNumPermis(request.getNumPermis());
            conducteur.setPhotoPermis(request.getPhotoPermis());
            conducteur.setStatut(StatutConducteur.EN_ATTENTE);

            user = conducteurRepository.save(conducteur);
        } else if (request.getRole() == Roles.PROPRIETAIRE) {
            Propriotaire proprietaire = new Propriotaire();
            proprietaire.setNom(request.getNom());
            proprietaire.setPrenom(request.getPrenom());
            proprietaire.setEmail(request.getEmail());
            proprietaire.setPassword(passwordEncoder.encode(request.getPassword()));
            proprietaire.setTelephone(request.getTelephone());
            proprietaire.setRole(Roles.PROPRIETAIRE);

            user = userRepository.save(proprietaire);
        } else {
            // Client simple (LIVREUR ou par défaut)
            user = new User();
            user.setNom(request.getNom());
            user.setPrenom(request.getPrenom());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setTelephone(request.getTelephone());
            user.setRole(request.getRole() != null ? request.getRole() : Roles.LIVREUR);

            user = userRepository.save(user);
        }

        // Générer le token JWT
        var userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        // Créer la réponse
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setTelephone(user.getTelephone());
        response.setRole(user.getRole());

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer le token
        var userDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        // Créer la réponse
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setTelephone(user.getTelephone());
        response.setRole(user.getRole());

        return response;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}