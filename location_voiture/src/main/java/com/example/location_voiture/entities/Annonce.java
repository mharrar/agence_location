package com.example.location_voiture.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "voiture")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String date_debut;

    @Column(nullable = false)
    private String date_fin;

    @ManyToOne
    @JoinColumn(name = "voiture_id", referencedColumnName = "id")
    private Voiture voiture;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;
}
