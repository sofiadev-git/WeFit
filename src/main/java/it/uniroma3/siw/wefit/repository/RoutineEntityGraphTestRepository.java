package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Routine;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoutineEntityGraphTestRepository extends Repository<Routine, Long> {

    @EntityGraph(attributePaths = {"eserciziDellaRoutine", "eserciziDellaRoutine.esercizio"})
    @Query("SELECT r FROM Routine r WHERE r.id = :id")
    Optional<Routine> findConEsercizi(@Param("id") Long id);
}
