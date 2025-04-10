package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agence,Long> {
}
