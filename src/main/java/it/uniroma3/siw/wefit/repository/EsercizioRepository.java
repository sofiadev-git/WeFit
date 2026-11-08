package it.uniroma3.siw.wefit.repository;
//Il repository si occupa di accedere ai dati, mentre il service organizza le operazioni dell'applicazione.
import it.uniroma3.siw.wefit.model.Esercizio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface EsercizioRepository extends JpaRepository<Esercizio,Long> {

    /* Alternativo a findById, che non ha lock*/
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Esercizio e WHERE e.id = :id")
    Optional<Esercizio> findPerModifica(@Param("id") Long id);


    /* Gli esercizi si dividono in 2 categorie:
    * 1. Esercizi default, creati dal admin, disponibili a tutti gli utenti in modo tale che il database non finisca con 1000 istanze addominali diverse per esempio
    * 2. Esercizi personalizzati, creati dai singoli utenti e visualizzabili/utilizzabili sono da questi.
    *
    * Quindi la lista di esercizi disponibili per un utente è data dagli esercizi default + personalizzati dell'utente.*/
    Page<Esercizio> findByAutore_Id(Long utenteId, Pageable pageable);

    List<Esercizio> findByEsercizioDefaultTrue();

    /* Serve per la pagina dei selezionabili, il punto 2 della creazione/modifica della routine, che usa la paginazione*/
    @Query("""
            SELECT e FROM Esercizio e
            WHERE e.esercizioDefault = true OR e.autore.id = :utenteId
            ORDER BY e.nome ASC, e.id ASC
            """)
    Page<Esercizio> findEserciziDisponibiliPaginati(@Param("utenteId") Long utenteId, Pageable pageable);

    /*Serve per i form*/
    @Query("""
            SELECT e FROM Esercizio e
            WHERE e.esercizioDefault = true OR e.autore.id = :utenteId
            ORDER BY e.nome ASC, e.id ASC
            """)
    List<Esercizio> findEserciziDisponibili(@Param("utenteId") Long utenteId);

    /* Serve per gli esercizi default nella pagina esercizi*/
    @Query("""
            SELECT e FROM Esercizio e WHERE e.esercizioDefault = true
            AND (:categoria = '' OR LOWER(TRIM(e.categoria)) = LOWER(:categoria))
            AND (:ricerca = '' OR LOCATE(LOWER(:ricerca), LOWER(e.nome)) > 0)
            """)
    Page<Esercizio> cercaNelCatalogo(@Param("categoria") String categoria, @Param("ricerca") String ricerca, Pageable pageable);

    /* Viene usato per il filtro nella pagina degli esercizi di default, ci restituisce la lista delle categorie*/
    @Query("""
            SELECT MIN(TRIM(e.categoria)) FROM Esercizio e WHERE e.esercizioDefault = true
            GROUP BY LOWER(TRIM(e.categoria)) ORDER BY LOWER(TRIM(e.categoria))
            """)
    List<String> findCategorieDefault();
}
