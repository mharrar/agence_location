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
public class Voiture {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ville;

    @Column(nullable = false)
    private int prix;

    @Column(nullable = false)
    private String type;


    @ManyToOne
    @JoinColumn(name = "agence_id", referencedColumnName = "id")
    private Agency agency;

}
