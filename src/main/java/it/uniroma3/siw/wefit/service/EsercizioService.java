package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.repository.EsercizioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EsercizioService {
    @Autowired
    EsercizioRepository esercizioRepository;

    /*un utente può visualizzare gli esercizi con EsercizioDefault=true o esercizi di cui è l'autore*/
    @Transactional(readOnly = true)
    public List<Esercizio> getEserciziUtente(Utente utente){
        if (utente == null ) {
            return List.of();
        } /*al posto di dare null da una lista vuota*/
        return esercizioRepository.findByAutore_Id(utente.getId());
    }

    @Transactional(readOnly = true)
    public List<Esercizio> getEserciziDefault(){
        return esercizioRepository.findByEsercizioDefaultTrue();
    }

    @Transactional(readOnly = true)
    public Esercizio getEsercizioDefaultOrAutore(Long id, Utente utente){
        Esercizio esercizio = esercizioRepository.findById(id).orElseThrow(()-> new NotFoundException("esercizio non trovato"));
        if(esercizio.isEsercizioDefault()){
            return esercizio;
        }
        if (utente != null && esercizio.getAutore().getId().equals(utente.getId())) {
            return esercizio;
        }
        throw new NotAllowedException("non hai accesso a questo esercizio");
    }

    @Transactional(readOnly = true)
    public Esercizio getEsercizioAutore(Long id,Utente utente){
        Esercizio esercizio = esercizioRepository.findById(id).orElseThrow(()-> new NotFoundException("esercizio non trovato"));
        checkAutore(esercizio,utente);
        return esercizio;
    }

    @Transactional
    public Esercizio createEsercizio(Esercizio esercizio, Utente autore) {
        esercizio.setAutore(autore);
        /* se un esercizio è creato da un admin allora quell'esercizio è disponibile a tutti come esercizio base*/
        boolean creatoDaAdmin =Utente.ADMIN_ROLE.equals(autore.getRole());
        esercizio.setEsercizioDefault(creatoDaAdmin);
        return esercizioRepository.save(esercizio);
    }

    @Transactional
    public Esercizio updateEsercizio(Esercizio esercizio, Long esercizioId, Utente utente){
        Esercizio e= getEsercizioDefaultOrAutore(esercizioId,utente);
        checkAutore(e,utente);
        e.setNome(esercizio.getNome());
        e.setCategoria(esercizio.getCategoria());
        e.setDescrizione(esercizio.getDescrizione());
        return esercizioRepository.save(e);
    }

    @Transactional
    public void deleteEsercizio(Long id, Utente utente){
        Esercizio e = getEsercizioDefaultOrAutore(id,utente);
        checkAutore(e,utente);
        esercizioRepository.deleteById(id);
        //aggiungere anche se partecipa a una routine
    }

    private void checkAutore(Esercizio esercizio, Utente utente){
        if(!esercizio.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare esercizi di utenti diversi dall'utente correntemente loggato.");
        }
    }

}
