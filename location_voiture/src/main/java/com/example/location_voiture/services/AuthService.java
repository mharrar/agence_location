package com.example.location_voiture.services;

import com.example.location_voiture.dto.ApiResponse;
import com.example.location_voiture.dto.LoginRequest;
import com.example.location_voiture.dto.RegisterRequest;
import com.example.location_voiture.entities.Client;
import com.example.location_voiture.entities.Role;
import com.example.location_voiture.entities.RoleName;
import com.example.location_voiture.entities.Utilisateur;
import com.example.location_voiture.exceptions.auth.*;
import com.example.location_voiture.exceptions.email.SendingEmailException;
import com.example.location_voiture.repositories.ClientRepository;
import com.example.location_voiture.repositories.RoleRepository;
import com.example.location_voiture.repositories.UtilisateurRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.location_voiture.security.JwtUtils;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public ApiResponse<String> register(RegisterRequest request) throws Exception {
        // Vérifier si l'utilisateur existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Un utilisateur avec cet email existe déjà.");
        }

        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(request.getEmail());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));
        utilisateur.setTelephone(request.getTelephone());
        utilisateur.setEnabled(false);  // L'utilisateur doit activer son compte
        utilisateur.setActivationToken(UUID.randomUUID().toString());

        // Vérifier et assigner les rôles envoyés dans la requête
        Set<Role> userRoles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(RoleName.valueOf(roleName.toUpperCase()))
                        .orElseThrow(() -> new RuntimeException("Rôle non trouvé : " + roleName));
                userRoles.add(role);
            }
        } else {
            // Si aucun rôle n'est spécifié, assigner ROLE_USER par défaut
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            userRoles.add(defaultRole);
        }

        // Assigner les rôles à l'utilisateur
        utilisateur.setRole(userRoles);

        // Sauvegarder l'utilisateur dans la base de données
        utilisateurRepository.save(utilisateur);

        if (userRoles.contains(roleRepository.findByName(RoleName.ROLE_CLIENT).orElseThrow())) {
            Client client = new Client();
            client.setUtilisateur(utilisateur);  // Associer le client à l'utilisateur
            clientRepository.save(client);  // Sauvegarder le client dans la base de données
        }

        // Générer le lien d'activation
        String activationLink = "http://localhost:8080/auth/activate?token=" + utilisateur.getActivationToken();

        // Charger et personnaliser le modèle d'email
        Map<String, String> emailVariables = Map.of("activationLink", activationLink);
        String emailContent = emailService.loadEmailTemplate("templates/emails/activation-email.html", emailVariables);

        // Envoyer l'email
        try {
            emailService.sendEmail(utilisateur.getEmail(), "Activation de votre compte", emailContent);
        } catch (MessagingException e) {
            throw new SendingEmailException("Erreur lors de l'envoi de l'email d'activation.");
        }

        return new ApiResponse<>("User registered successfully! Please check your email to activate your account.", HttpStatus.OK.value());
    }

    public String login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // If authentication is successful, generate JWT token
        if (authentication.isAuthenticated()) {
            Optional<Utilisateur> user = utilisateurRepository.findByEmail(loginRequest.getEmail());
            if (!user.get().isEnabled()) {
                throw new AccountIsNotEnabledException("Your account is not activated. Please check your email for the activation link.");
            }
            return jwtUtils.generateToken(authentication);
        } else {
            throw new InvalidCredentialsException("Invalid email or password.");
        }
    }

    public ApiResponse<String>  activateAccount(String token) {
        Optional<Utilisateur> userOptional = utilisateurRepository.findByActivationToken(token);

        if (userOptional.isEmpty()) {
            throw new InvalidTokenException("Token invalide !");
        }

        Utilisateur utilisateur = userOptional.get();
        utilisateur.setEnabled(true);
        utilisateur.setActivationToken(null);
        utilisateurRepository.save(utilisateur);

        return new ApiResponse<>("User registered successfully! Please check your email to activate your account.", HttpStatus.OK.value());
    }

    public void forgotPassword(String email) throws Exception {
        // Check if the user exists with the provided email
        Optional<Utilisateur> userOptional = utilisateurRepository.findByEmail(email);
        System.out.println(userOptional);
        // Throw a custom exception if the email is not found
        if (userOptional.isEmpty()) {
            throw new EmailNotFoundException("Email not found!");
        }

        Utilisateur user = userOptional.get();
        String resetToken = UUID.randomUUID().toString();
        user.setActivationToken(resetToken);
        utilisateurRepository.save(user);

        // Générer le lien de réinitialisation
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + resetToken;

        // Charger et personnaliser le modèle d'email
        Map<String, String> emailVariables = Map.of("resetLink", resetLink);
        String emailContent = emailService.loadEmailTemplate("templates/emails/reset-password-email.html", emailVariables);
        // Send the reset email
        emailService.sendEmail(user.getEmail(), "Réinitialisation du mot de passe", emailContent);
    }

    public void resetPassword(String token, String newPassword) {
        // Check if the user exists with the provided token
        Optional<Utilisateur> userOptional = utilisateurRepository.findByActivationToken(token);

        // If token is invalid, throw a custom exception
        if (userOptional.isEmpty()) {
            throw new InvalidTokenException("Token invalide !");
        }

        Utilisateur user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));  // Set new password
        user.setActivationToken(null);  // Remove token after use
        utilisateurRepository.save(user);
    }
}
