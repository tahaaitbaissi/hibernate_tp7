package org.example.service;

import org.example.model.Auteur;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import java.util.List;

public class PerformanceTestService {

    private EntityManagerFactory emf;

    public PerformanceTestService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public long testPerformanceSansCache() {
        EntityManager em = emf.createEntityManager();
        long start = System.currentTimeMillis();

        TypedQuery<Auteur> query = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        List<Auteur> auteurs = query.getResultList();

        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        long duree = end - start;
        System.out.println("Duree sans cache : " + duree + " ms");
        em.close();
        return duree;
    }

    public long testPerformanceAvecJoinFetch() {
        EntityManager em = emf.createEntityManager();
        long start = System.currentTimeMillis();

        TypedQuery<Auteur> query = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        List<Auteur> auteurs = query.getResultList();

        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        long duree = end - start;
        System.out.println("Duree avec JOIN FETCH : " + duree + " ms");
        em.close();
        return duree;
    }

    public long testPerformanceAvecEntityGraph() {
        EntityManager em = emf.createEntityManager();
        long start = System.currentTimeMillis();

        EntityGraph<?> graph = em.getEntityGraph("Auteur.livres");
        TypedQuery<Auteur> query = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        query.setHint("javax.persistence.fetchgraph", graph);
        List<Auteur> auteurs = query.getResultList();

        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        long duree = end - start;
        System.out.println("Duree avec EntityGraph : " + duree + " ms");
        em.close();
        return duree;
    }

    public long testPerformanceAvecCache() {
        testPerformanceAvecJoinFetch();

        EntityManager em = emf.createEntityManager();
        long start = System.currentTimeMillis();

        TypedQuery<Auteur> query = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        query.setHint("org.hibernate.cacheable", true);
        List<Auteur> auteurs = query.getResultList();

        for (Auteur auteur : auteurs) {
            auteur.getLivres().size();
        }

        long end = System.currentTimeMillis();
        long duree = end - start;
        System.out.println("Duree avec cache (lecture 2) : " + duree + " ms");
        em.close();
        return duree;
    }
}
