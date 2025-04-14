package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Utilisateur;
import com.example.location_voiture.entities.Voiture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoitureRepository extends JpaRepository<Voiture,Long>  {
}
