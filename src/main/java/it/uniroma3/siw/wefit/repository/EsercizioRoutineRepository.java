package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EsercizioRoutineRepository extends JpaRepository<EsercizioRoutine,Long> {

    boolean existsByEsercizio_Id(Long esercizioId);

    List<EsercizioRoutine> findByRoutine_Id(Long routineId);

    Optional<EsercizioRoutine> findByRoutine_IdAndId(Long routineId, Long id);
}
