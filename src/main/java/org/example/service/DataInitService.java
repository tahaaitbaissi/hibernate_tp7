package org.example.service;

import org.example.model.Auteur;
import org.example.model.Livre;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DataInitService {

    private EntityManagerFactory emf;

    public DataInitService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void initData() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        String[] noms = {"Yassine", "Karim", "Salma", "Omar", "Sara", "Mehdi", "Nadia", "Bilal", "Rania", "Adam"};

        for (int i = 1; i <= 10; i++) {
            Auteur auteur = new Auteur();
            auteur.setNomPlume(noms[i - 1] + " " + i);

            for (int j = 1; j <= 5; j++) {
                Livre livre = new Livre();
                livre.setTitreOuvrage("Python " + j + " de " + noms[i - 1]);
                auteur.ajouterLivre(livre);
            }

            em.persist(auteur);
        }

        em.getTransaction().commit();
        em.close();
    }
}
