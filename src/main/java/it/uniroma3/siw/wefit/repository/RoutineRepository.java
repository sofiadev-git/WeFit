package it.uniroma3.siw.wefit.repository;

import it.uniroma3.siw.wefit.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine,Long> {

    List<Routine> findByAutore_Id(Long autoreId);

    Routine findRoutineByAutore_Id(Long autoreId,Long routineId);
}
