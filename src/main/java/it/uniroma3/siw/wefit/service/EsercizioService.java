package it.uniroma3.siw.wefit.service;

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

    @Transactional(readOnly = true)
    public List<Esercizio> getAllEsercizi(){
       return esercizioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Esercizio getEsercizio(Long id){
        return esercizioRepository.findById(id).orElseThrow(()-> new NotFoundException("esercizio non trovato"));
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
    public Esercizio updateEsercizio(Esercizio esercizio, Long esercizioId){
        Esercizio e= getEsercizio(esercizioId);
        e.setNome(esercizio.getNome());
        e.setCategoria(esercizio.getCategoria());
        e.setDescrizione(esercizio.getDescrizione());
        return esercizioRepository.save(e);
    }

    @Transactional
    public void deleteEsercizio(Long id){
        esercizioRepository.deleteById(id);
        //aggiungere anche se partecipa a una routine
    }

}
