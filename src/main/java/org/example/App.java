package org.example;

import org.example.service.DataInitService;
import org.example.service.PerformanceTestService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-cache-pu");

        DataInitService init = new DataInitService(emf);
        init.initData();

        PerformanceTestService perf = new PerformanceTestService(emf);

        System.out.println("--- Test sans cache ---");
        perf.testPerformanceSansCache();

        System.out.println("--- Test avec JOIN FETCH ---");
        perf.testPerformanceAvecJoinFetch();

        System.out.println("--- Test avec EntityGraph ---");
        perf.testPerformanceAvecEntityGraph();

        System.out.println("--- Test avec cache de second niveau ---");
        perf.testPerformanceAvecCache();

        emf.close();
    }
}
