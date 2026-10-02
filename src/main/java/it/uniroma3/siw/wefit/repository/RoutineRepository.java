package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine,Long> {

    List<Routine> findByAutore_Id(Long autoreId);

    Routine findRoutineByAutore_Id(Long autoreId,Long routineId);

    @Modifying /*non legge dati ma li modifica (elimina)*/
    @Query(value = "DELETE FROM user_bookmarked_routines WHERE routine_id = :routineId",nativeQuery = true)
    /*Cerca l'id delle routine e la elimina dalla tabella, altrimenti servirebbe un giro enorme con for*/
    void deleteRoutineSalvataByRoutineId(@Param("routineId") Long routineId);
}
