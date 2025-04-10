package com.example.location_voiture.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity  // Changement de MappedSuperclass à Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Assurez-vous que cette classe a un identifiant unique
    private Long id;



    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Long telephone;  // Utilisation de Long pour gérer les valeurs nulles


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> role;
    private boolean enabled = false;

    private String activationToken;
}
