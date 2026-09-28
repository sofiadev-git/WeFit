package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.repository.EsercizioRoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EsercizioRoutineService {

    @Autowired
    EsercizioRoutineRepository esercizioRoutineRepository;

    @Autowired
    EsercizioService esercizioService;

    @Autowired
    RoutineService routineService;


    @Transactional(readOnly = true)
    public List<EsercizioRoutine> getEserciziRoutine(Long routineId){
        return esercizioRoutineRepository.findByRoutine_Id(routineId);
    }

    @Transactional(readOnly = true)
    public EsercizioRoutine getEsercizioRoutine(Long routineId, Long id){
        return esercizioRoutineRepository.findByRoutine_IdAndId(routineId,id).orElseThrow(()->new NotFoundException("esercizio non trovato nella routine."));
    }

    @Transactional
    public EsercizioRoutine createEsercizioRoutine(Long routineId, Long esercizioId, EsercizioRoutine esercizioRoutine){
        Routine r = routineService.getRoutine(routineId);
        Esercizio e = esercizioService.getEsercizio(esercizioId);
        esercizioRoutine.setRoutine(r);
        esercizioRoutine.setEsercizio(e);
        return esercizioRoutineRepository.save(esercizioRoutine);
    }

    @Transactional
    public EsercizioRoutine updateEsercizioRoutine(Long routineId, Long id, EsercizioRoutine esercizioRoutine){
        Routine r = routineService.getRoutine(routineId);
        EsercizioRoutine e = getEsercizioRoutine(routineId,id);
        e.setEsercizio(esercizioRoutine.getEsercizio());
        e.setRipetizioni(esercizioRoutine.getRipetizioni());
        e.setNumeroSet(esercizioRoutine.getNumeroSet());
        return esercizioRoutineRepository.save(e);
    }

    @Transactional
    public void deleteEsercizioRoutine(Long id){
        esercizioRoutineRepository.deleteById(id);
    }



}
