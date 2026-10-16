package org.example;

import org.example.model.Auteur;
import org.example.service.DataInitService;
import org.example.service.PerformanceTestService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.Assert.*;

public class CacheTest {

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void init() {
        emf = Persistence.createEntityManagerFactory("hibernate-cache-pu");
        new DataInitService(emf).initData();
    }

    @AfterClass
    public static void close() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testDataInit() {
        EntityManager em = emf.createEntityManager();
        long nbAuteurs = em.createQuery("SELECT COUNT(a) FROM Auteur a", Long.class).getSingleResult();
        long nbLivres = em.createQuery("SELECT COUNT(l) FROM Livre l", Long.class).getSingleResult();
        assertEquals(10, nbAuteurs);
        assertEquals(50, nbLivres);
        em.close();
    }

    @Test
    public void testJoinFetch() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Auteur> q = em.createQuery("SELECT DISTINCT a FROM Auteur a JOIN FETCH a.livres", Auteur.class);
        List<Auteur> list = q.getResultList();
        assertEquals(10, list.size());
        for (Auteur a : list) {
            assertEquals(5, a.getLivres().size());
        }
        em.close();
    }

    @Test
    public void testEntityGraph() {
        EntityManager em = emf.createEntityManager();
        EntityGraph<?> graph = em.getEntityGraph("Auteur.livres");
        TypedQuery<Auteur> q = em.createQuery("SELECT a FROM Auteur a", Auteur.class);
        q.setHint("javax.persistence.fetchgraph", graph);
        List<Auteur> list = q.getResultList();
        assertEquals(10, list.size());
        for (Auteur a : list) {
            assertEquals(5, a.getLivres().size());
        }
        em.close();
    }

    @Test
    public void testSecondLevelCache() {
        EntityManager em1 = emf.createEntityManager();
        Auteur premier = em1.createQuery("SELECT a FROM Auteur a ORDER BY a.id", Auteur.class)
                .setMaxResults(1).getSingleResult();
        Long id = premier.getId();
        String nom = premier.getNomPlume();
        em1.close();

        EntityManager em2 = emf.createEntityManager();
        Auteur cached = em2.find(Auteur.class, id);
        assertNotNull(cached);
        assertEquals(nom, cached.getNomPlume());
        em2.close();

        PerformanceTestService perf = new PerformanceTestService(emf);
        long duree = perf.testPerformanceAvecCache();
        assertTrue(duree >= 0);
    }
}
