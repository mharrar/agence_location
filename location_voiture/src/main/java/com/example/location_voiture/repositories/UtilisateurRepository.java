package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
}
