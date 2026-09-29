package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.dto.RoutineDetailsDto;
import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.repository.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoutineService {
    @Autowired
    RoutineRepository routineRepository;

    @Transactional(readOnly = true)
    public List<Routine> getAllRoutines(){
        return routineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Routine getRoutine(Long id){
        return routineRepository.findById(id).orElseThrow(()-> new NotFoundException("routine non trovata"));
    }

    @Transactional(readOnly = true)
    public List<Routine> getRoutinesUtente(Long utenteId){
        return routineRepository.findByAutore_Id(utenteId);
    }

    @Transactional(readOnly = true)
    public Routine getRoutineUtente(Long utenteId, Long id){
        return routineRepository.findRoutineByAutore_Id(utenteId, id);
    }

    /*@Transactional
    public RoutineDetailsDto getRoutineDetalis(Long id){
        //da completare
    }*/

    @Transactional
    public Routine createRoutine(Routine routine, Utente autore){
        routine.setAutore(autore);
        return  routineRepository.save(routine);
    }

    @Transactional
    public Routine updateRoutine(Long id,Routine routine, Utente utente){
        Routine r= getRoutine(id);
        checkAutore(r,utente);
        r.setDescrizione(routine.getDescrizione());
        r.setNome(routine.getNome());
        // r.setEserciziDellaRoutine(routine.getEserciziDellaRoutine()); no sono gestiti dal loro service
        return routineRepository.save(r);
    }

    @Transactional
    public void deleteRoutine(Long id, Utente utenteId){
        Routine r = getRoutine(id);
        checkAutore(r,utenteId);
        routineRepository.deleteById(id);
    }

    private void checkAutore(Routine routine, Utente utente){
        if(!routine.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare routine di utenti diversi dall'utente correntemente loggato.");
        }
    }



}
