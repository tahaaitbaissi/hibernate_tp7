package org.example.model;

import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "ouvrages")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titre_ouvrage", length = 150)
    private String titreOuvrage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecrivain_id")
    private Auteur auteur;

    public Livre() {
    }

    public Livre(String titreOuvrage) {
        this.titreOuvrage = titreOuvrage;
    }

    public Long getId() {
        return id;
    }

    public String getTitreOuvrage() {
        return titreOuvrage;
    }

    public void setTitreOuvrage(String titreOuvrage) {
        this.titreOuvrage = titreOuvrage;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }
}
