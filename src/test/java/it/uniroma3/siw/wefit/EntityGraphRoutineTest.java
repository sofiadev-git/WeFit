package it.uniroma3.siw.wefit;

import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.repository.RoutineEntityGraphTestRepository;
import it.uniroma3.siw.wefit.repository.RoutineRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Confronto didattico: caricamento ordinario LAZY vs @EntityGraph.
 * Nessuna modifica a repository/service/controller di produzione.
 * Usa le routine gia' presenti nel database, senza scrivere nulla.
 */
@SpringBootTest(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never",
        "spring.jpa.show-sql=false",
        "logging.level.root=ERROR",
        "logging.level.org.hibernate.SQL=OFF",
        "logging.level.org.hibernate.orm.jdbc.bind=OFF",
        "spring.main.banner-mode=off"
})
@Transactional(readOnly = true)
class EntityGraphRoutineTest {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineEntityGraphTestRepository routineEntityGraphTestRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void confrontaLazyEdEntityGraph() {
        // Selezione del caso: una routine che contiene almeno un esercizio.
        // Questa query e' esclusa da entrambe le misurazioni.
        Long routineId = entityManager.createQuery(
                "SELECT MIN(er.routine.id) FROM EsercizioRoutine er", Long.class
        ).getSingleResult();

        assumeTrue(routineId != null,
                "Per il test serve almeno una routine con un esercizio associato");

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);

        // 1. Caricamento normale: findById e accesso alla collezione LAZY.
        entityManager.clear();
        statistics.clear();
        long inizioLazy = System.nanoTime();
        Routine routineLazy = routineRepository.findById(routineId).orElseThrow();
        boolean collezioneCaricataPrimaLazy = entityManagerFactory.getPersistenceUnitUtil()
                .isLoaded(routineLazy, "eserciziDellaRoutine");
        List<String> datiLazy = datiEsercizi(routineLazy);
        double millisecondiLazy = (System.nanoTime() - inizioLazy) / 1_000_000.0;
        long queryLazy = statistics.getPrepareStatementCount();
        long entitaLazy = statistics.getEntityLoadCount();

        // 2. Stessi dati: caricamento con @EntityGraph (repository solo di test).
        entityManager.clear();
        statistics.clear();
        long inizioGraph = System.nanoTime();
        Routine routineGraph = routineEntityGraphTestRepository.findConEsercizi(routineId).orElseThrow();
        boolean collezioneCaricataPrimaGraph = entityManagerFactory.getPersistenceUnitUtil()
                .isLoaded(routineGraph, "eserciziDellaRoutine");
        List<String> datiGraph = datiEsercizi(routineGraph);
        double millisecondiGraph = (System.nanoTime() - inizioGraph) / 1_000_000.0;
        long queryGraph = statistics.getPrepareStatementCount();
        long entitaGraph = statistics.getEntityLoadCount();

        assertEquals(datiLazy, datiGraph,
                "Le due strategie devono restituire gli stessi esercizi, set e ripetizioni");
        assertTrue(collezioneCaricataPrimaGraph,
                "L'EntityGraph deve caricare in anticipo eserciziDellaRoutine");

        System.out.println();
        System.out.println("=== WEFIT: LAZY vs ENTITY GRAPH (SOLO TEST) ===");
        System.out.println("Routine ID: " + routineId);
        System.out.println("Esercizi associati: " + datiLazy.size());
        System.out.println();
        System.out.println("--- Strategia 1: findById (LAZY) ---");
        System.out.println("Collezione gia' caricata prima dell'accesso: " + collezioneCaricataPrimaLazy);
        System.out.println("Query SQL eseguite: " + queryLazy);
        System.out.println("Entita' caricate: " + entitaLazy);
        System.out.printf("Tempo indicativo: %.2f ms%n", millisecondiLazy);
        System.out.println();
        System.out.println("--- Strategia 2: @EntityGraph (solo test) ---");
        System.out.println("Collezione gia' caricata prima dell'accesso: " + collezioneCaricataPrimaGraph);
        System.out.println("Query SQL eseguite: " + queryGraph);
        System.out.println("Entita' caricate: " + entitaGraph);
        System.out.printf("Tempo indicativo: %.2f ms%n", millisecondiGraph);
        System.out.println();
        System.out.println("Stessi dati: SI");
        System.out.println("Il tempo dipende anche da cache e database; confrontare soprattutto le query.");
        System.out.println("Nessun dato modificato. Il sito usa ancora il repository originale.");
        System.out.println("------------------------------------------------");
    }

    private List<String> datiEsercizi(Routine routine) {
        // Eseguiamo la stessa lettura completa per entrambe le strategie.
        return routine.getEserciziDellaRoutine().stream()
                .map(this::datiEsercizio)
                .sorted()
                .toList();
    }

    private String datiEsercizio(EsercizioRoutine er) {
        return er.getId() + "|" + er.getEsercizio().getId() + "|"
                + er.getEsercizio().getNome() + "|" + er.getEsercizio().getCategoria()
                + "|" + er.getNumeroSet() + "|" + er.getRipetizioni();
    }
}
