package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Esercizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EsercizioRepository extends JpaRepository<Esercizio,Long> {

    /* Gli esercizi si dividono in 2 categorie:
    * 1. Esercizi default, creati dal admin, disponibili a tutti gli utenti in modo tale che il database non finisca con 1000 istanze addominali diverse per esempio
    * 2. Esercizi personalizzati, creati dai singoli utenti e visualizzabili/utilizzabili sono da questi.
    *
    * Quindi la lista di esercizi disponibili per un utente è data dagli esercizi default + personalizzati dell'utente.*/
    List<Esercizio> findByAutore_Id(Long utenteId);

    List<Esercizio> findByEsercizioDefaultTrue();
    @Query("""
            SELECT e FROM Esercizio e
            WHERE e.esercizioDefault = true OR e.autore.id = :utenteId
            ORDER BY e.nome ASC
            """)
    List<Esercizio> findEserciziDisponibili(@Param("utenteId") Long utenteId);
}
