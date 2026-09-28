package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UtenteRepository extends JpaRepository<Utente,Long> {

    @Modifying
    @Query(value = "DELETE FROM user_bookmarked_routines WHERE routine_id = :routineId",nativeQuery = true)
    void deleteRoutineSalvataByRoutineId(@Param("routineId") Long routineId);

    boolean existsUtenteByUsername(String normalizedUsername);

    Optional<Utente> findUtenteByUsername(String username);
}
