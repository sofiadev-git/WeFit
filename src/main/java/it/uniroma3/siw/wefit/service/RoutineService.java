package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.dto.EsercizioDellaDto;
import it.uniroma3.siw.wefit.dto.RoutineDetailsDto;
import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.repository.RoutineRepository;
import it.uniroma3.siw.wefit.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RoutineService {
    @Autowired
    RoutineRepository routineRepository;
    @Autowired
    private UtenteRepository utenteRepository;

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

    @Transactional(readOnly = true)
    public RoutineDetailsDto getRoutineDetails(Long id, Utente utenteCorrente) {
        /*L'utente serve per mostrare se per quell'utente la routine è bookmarked o meno*/
        Routine routine = getRoutine(id);
        boolean bookmarked = utenteCorrente != null && utenteCorrente.getBookmarkedRoutines().contains(routine);
        List<EsercizioDellaDto> esercizi = new ArrayList<>();
        for (EsercizioRoutine er : routine.getEserciziDellaRoutine()) {
            esercizi.add(new EsercizioDellaDto(
                    er.getId(),
                    er.getEsercizio().getNome(),
                    er.getEsercizio().getDescrizione(),
                    er.getNumeroSet(),
                    er.getRipetizioni()
            ));
        }

        return new RoutineDetailsDto(
                routine.getId(),
                routine.getNome(),
                routine.getDescrizione(),
                routine.getAutore().getUsername(),
                esercizi.size(),
                bookmarked,
                esercizi
        );
    }

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
        routineRepository.deleteRoutineSalvataByRoutineId(r.getId());
        routineRepository.deleteById(id);
    }

    private void checkAutore(Routine routine, Utente utente){
        if(!routine.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare routine di utenti diversi dall'utente correntemente loggato.");
        }
    }

    /* Metodi per gestire i bookmark*/

    @Transactional
    public RoutineDetailsDto toggleBookmark(long id, Utente utente){
        Routine routine= getRoutine(id);
        /*se premo ed era salvato lo rimuove e restituisce true, altrimenti da false se non lo trova*/
        if(utente.getBookmarkedRoutines().remove(routine)){
            /*lo ha eliminato, non serve fare nulla*/
        }
        else{
            /* L'utente non lo ha già salvato, lo salva adesso*/
            utente.getBookmarkedRoutines().add(routine);
        }

        /*
        * E' equivalente a:
        * if (utente.getBookmarkedRoutines().contains(routine)) {
        *   utente.getBookmarkedRoutines().remove(routine);
        * }
        * else {
        *   utente.getBookmarkedRoutines().add(routine);}*/
        utenteRepository.save(utente); // un Utente salvo i bookmark di quell'utente

        return getRoutineDetails(id,utente);
    }

    /* Pagina bookmark utente*/
    @Transactional(readOnly = true)
    public Set<Routine> getBookmarkedRoutines(Utente utente) {
        return utente.getBookmarkedRoutines();
    }
}
