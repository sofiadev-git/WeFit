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
    public List<Routine> getRoutinesUtente(Utente utente){
        return routineRepository.findByAutore_Id(utente.getId());
    }

    @Transactional(readOnly = true)
    public Routine getRoutineUtente(Utente utente, Long id){
        return routineRepository.findRoutineByAutore_IdAndId(utente.getId(), id);
    }

    @Transactional(readOnly = true)
    public RoutineDetailsDto getRoutineDetails(Long id, Utente utenteCorrente) {
        /*L'utente serve per mostrare se per quell'utente la routine è bookmarked o meno*/
        Routine routine = getRoutine(id);
        boolean bookmarked = utenteCorrente != null && !routine.getAutore().getId().equals(utenteCorrente.getId())
                && routineRepository.findBookmarkedRoutineIds(utenteCorrente.getId()).contains(routine.getId());
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
    public void deleteRoutine(Long id, Utente utente){
        Routine r = getRoutine(id);
        checkAutore(r,utente);
        routineRepository.deleteRoutineSalvataByRoutineId(r.getId());
        routineRepository.deleteById(id);
    }

    private void checkAutore(Routine routine, Utente utente){
        if(!routine.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare routine di utenti diversi dall'utente correntemente loggato.");
        }
    }

    /* Le routine dell'autore non possono essere salvate nei propri bookmark. */
    @Transactional
    public RoutineDetailsDto toggleBookmark(long id, Utente utente) {
        if (utente == null) {
            throw new NotAllowedException("Accedi per salvare una routine.");
        }
        if (Utente.ADMIN_ROLE.equals(utente.getRole())) {
            throw new NotAllowedException("I bookmark sono disponibili nell'area personale degli utenti.");
        }

        Routine routine = getRoutine(id);
        if (routine.getAutore().getId().equals(utente.getId())) {
            throw new NotAllowedException("Non puoi salvare una routine di cui sei autore.");
        }

        Utente utenteGestito = utenteRepository.findById(utente.getId())
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
        if (!utenteGestito.getBookmarkedRoutines().remove(routine)) {
            utenteGestito.getBookmarkedRoutines().add(routine);
        }
        utenteRepository.save(utenteGestito);
        return getRoutineDetails(id, utenteGestito);
    }

    @Transactional(readOnly = true)
    public List<Routine> getBookmarkedRoutines(Utente utente) {
        if (utente == null) {
            throw new NotAllowedException("Utente non autenticato");
        }
        return routineRepository.findBookmarkedRoutinesOfOtherAuthors(utente.getId());
    }

    @Transactional(readOnly = true)
    public Set<Long> getBookmarkedRoutineIds(Utente utente) {
        if (utente == null) {
            return Set.of();
        }
        return routineRepository.findBookmarkedRoutineIds(utente.getId());
    }
}
