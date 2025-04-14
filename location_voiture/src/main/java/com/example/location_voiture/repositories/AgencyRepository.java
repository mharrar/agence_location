package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agency,Long> {
}
