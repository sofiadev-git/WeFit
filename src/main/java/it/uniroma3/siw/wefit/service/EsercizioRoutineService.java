package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
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

    @Transactional(readOnly = true)
    public EsercizioRoutine getEsercizioRoutineGlobal(Long id){
        return  esercizioRoutineRepository.findById(id).orElseThrow(()->new NotFoundException("esercizio non trovato nella routine."));
    }

    @Transactional
    public EsercizioRoutine createEsercizioRoutine(Long routineId, Long esercizioId, EsercizioRoutine esercizioRoutine, Utente utente){
        Routine r = routineService.getRoutine(routineId);
        Esercizio e = esercizioService.getEsercizioDefaultOrAutore(esercizioId,utente);
        checkAutoreRoutine(r,utente);
        checkAutoreEsercizio(e,utente);
        esercizioRoutine.setRoutine(r);
        esercizioRoutine.setEsercizio(e);
        return esercizioRoutineRepository.save(esercizioRoutine);
    }

    @Transactional
    public EsercizioRoutine updateEsercizioRoutine(Long routineId, Long associazioneId, Long nuovoEsercizioId, EsercizioRoutine nuoviValori, Utente utente) {
        EsercizioRoutine er = getEsercizioRoutine(routineId, associazioneId);
        checkAutore(er, utente);

        Esercizio esercizio = esercizioService.getEsercizioDefaultOrAutore(nuovoEsercizioId, utente);

        er.setEsercizio(esercizio);
        er.setNumeroSet(nuoviValori.getNumeroSet());
        er.setRipetizioni(nuoviValori.getRipetizioni());

        return esercizioRoutineRepository.save(er);
    }

    @Transactional
    public void deleteEsercizioRoutine(Long routineId, Long associazioneId, Utente utente) {
        EsercizioRoutine er = getEsercizioRoutine(routineId, associazioneId);
        checkAutore(er, utente);
        esercizioRoutineRepository.delete(er);
    }

    private void checkAutore(EsercizioRoutine esercizioRoutine, Utente utente){
        if(!esercizioRoutine.getRoutine().getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare esercizi in routine di utenti diversi dall'utente correntemente loggato.");
        }
    }

    private void checkAutoreRoutine(Routine routine, Utente utente){
        if(!routine.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare routine di utenti diversi dall'utente correntemente loggato.");
        }
    }

    private void checkAutoreEsercizio(Esercizio esercizio, Utente utente){
        if(!esercizio.isEsercizioDefault() && !esercizio.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare esercizi di utenti diversi dall'utente correntemente loggato.");
        }
    }



}
