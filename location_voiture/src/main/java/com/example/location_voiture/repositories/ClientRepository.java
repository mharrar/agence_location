package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {
}
