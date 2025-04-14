package com.example.location_voiture.services;


import com.example.location_voiture.entities.Agency;
import com.example.location_voiture.exceptions.agence.AgenceNotFoundException;
import com.example.location_voiture.repositories.AgenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgenceService {

    private final AgenceRepository agenceRepository;

    public AgenceService(AgenceRepository actorRepository) {
        this.agenceRepository = actorRepository;}

    public void saveAgence(Agency agency) {
        // La méthode 'save' de JpaRepository permet de sauvegarder un acteur dans la base de données.
        agenceRepository.save(agency);
    }


    public void updateAgence(Long id, Agency agency) {
        Agency existingAgency = agenceRepository.findById(id)
                .orElseThrow(() -> new AgenceNotFoundException("Agence not found"));

        // Mise à jour des informations de l'acteur avec les nouvelles données.
           existingAgency.setNom_agence(agency.getNom_agence());
           existingAgency.setNombre_voiture(agency.getNombre_voiture());
           existingAgency.setAdresse(agency.getAdresse());

        // Sauvegarde de l'acteur mis à jour dans la base de données.
        agenceRepository.save(existingAgency);
    }

    // Méthode permettant de supprimer un acteur de la base de données en utilisant son identifiant.
    public void deleteAgence(Long id) {
        // Suppression de l'acteur à l'aide de son identifiant.
        agenceRepository.deleteById(id);
    }

    // Méthode permettant de récupérer la liste de tous les acteurs.
    // Cette méthode retourne une liste de tous les acteurs présents dans la base de données.
    public List<Agency> getAllAgences() {
        // La méthode 'findAll' de JpaRepository permet de récupérer tous les enregistrements d'acteurs.
        return agenceRepository.findAll();
    }
}