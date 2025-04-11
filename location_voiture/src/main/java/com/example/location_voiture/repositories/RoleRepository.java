package com.example.location_voiture.repositories;

import com.example.location_voiture.entities.Role;
import com.example.location_voiture.entities.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName name);
}
