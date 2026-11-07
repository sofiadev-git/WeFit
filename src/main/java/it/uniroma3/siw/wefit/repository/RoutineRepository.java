package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Routine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RoutineRepository extends JpaRepository<Routine,Long> {

    Page<Routine> findByNomeContainingIgnoreCaseOrAutore_UsernameContainingIgnoreCase(String nome, String username, Pageable pageable);

    Page<Routine> findByAutore_Id(Long autoreId, Pageable pageable);

    @Query("select r from Utente u join u.bookmarkedRoutines r where u.id = :utenteId and r.autore.id <> :utenteId order by r.nome, r.id")
    Page<Routine> findBookmarkedRoutinesOfOtherAuthors(@Param("utenteId") Long utenteId, Pageable pageable);

    @Query("select r.id from Utente u join u.bookmarkedRoutines r where u.id = :utenteId and r.autore.id <> :utenteId")
    Set<Long> findBookmarkedRoutineIds(@Param("utenteId") Long utenteId);

    Routine findRoutineByAutore_IdAndId(Long autoreId, Long routineId);

    @Modifying /*non legge dati ma li modifica (elimina)*/
    @Query(value = "DELETE FROM user_bookmarked_routines WHERE routine_id = :routineId",nativeQuery = true)
    /*Cerca l'id delle routine e la elimina dalla tabella, altrimenti servirebbe un giro enorme con for*/
    void deleteRoutineSalvataByRoutineId(@Param("routineId") Long routineId);
}
