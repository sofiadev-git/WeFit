package it.uniroma3.siw.wefit.service;

import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.repository.EsercizioRepository;
import it.uniroma3.siw.wefit.repository.EsercizioRoutineRepository;
import it.uniroma3.siw.wefit.exception.NotValidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EsercizioService {

    private static final int ESERCIZI_PER_PAGINA = 6;

    @Autowired
    EsercizioRepository esercizioRepository;

    @Value("${wefit.upload-dir}")
    private String cartellaFoto;

    @Autowired
    EsercizioRoutineRepository esercizioRoutineRepository;

    /*un utente può visualizzare gli esercizi con EsercizioDefault=true o esercizi di cui è l'autore*/
    @Transactional(readOnly = true)
    public Page<Esercizio> getEserciziUtente(Utente utente,int page){
        Pageable pageable = PageRequest.of(page, ESERCIZI_PER_PAGINA, Sort.by("id"));
        return esercizioRepository.findByAutore_Id(utente.getId(),pageable);
    }


    @Transactional(readOnly = true)
    public Page<Esercizio> getEserciziDefault(int page, String categoria, String ricerca) {
        Pageable pageable = PageRequest.of(page, ESERCIZI_PER_PAGINA, Sort.by("id"));
        return esercizioRepository.cercaNelCatalogo(categoria, ricerca, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getCategorieDefault() {
        return esercizioRepository.findCategorieDefault();
    }

    /*usato nel form per mostrare tutti gli esercizi selezionabili*/
    @Transactional(readOnly = true)
    public List<Esercizio> getEserciziDisponibili(Utente utente){
        if(utente == null){
            throw  new NotAllowedException("Utente non autenticato");//
        }
        return esercizioRepository.findEserciziDisponibili(utente.getId());
    }

    @Transactional(readOnly = true)
    public Page<Esercizio> getEserciziDisponibili(Utente utente,int page){
        if(utente == null){
            throw  new NotAllowedException("Utente non autenticato");//
        }
        Pageable pageable = PageRequest.of(page, ESERCIZI_PER_PAGINA, Sort.by("id"));
        return esercizioRepository.findEserciziDisponibiliPaginati(utente.getId(),pageable);
    }

    @Transactional(readOnly = true)
    public Esercizio getEsercizioDefaultOrAutore(Long id, Utente utente){
        Esercizio esercizio = esercizioRepository.findById(id).orElseThrow(()-> new NotFoundException("esercizio non trovato"));
        return verificaVisibilita(esercizio, utente);
    }

    private Esercizio verificaVisibilita(Esercizio esercizio, Utente utente) {
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
    public Esercizio createEsercizio(Esercizio esercizio, Utente autore, MultipartFile foto) {
        esercizio.setAutore(autore);
        /* se un esercizio è creato da un admin allora quell'esercizio è disponibile a tutti come esercizio base*/
        boolean creatoDaAdmin =Utente.ADMIN_ROLE.equals(autore.getRole());
        esercizio.setEsercizioDefault(creatoDaAdmin);
        salvaFoto(esercizio, foto);
        return esercizioRepository.save(esercizio);
    }

    @Transactional
    public Esercizio updateEsercizio(Esercizio esercizio, Long esercizioId, Utente utente, MultipartFile foto){
        Esercizio e = esercizioRepository.findPerModifica(esercizioId).orElseThrow(() -> new NotFoundException("esercizio non trovato"));
        checkAutore(e,utente);
        salvaFoto(e, foto);
        e.setNome(esercizio.getNome());
        e.setCategoria(esercizio.getCategoria());
        e.setDescrizione(esercizio.getDescrizione());
        return esercizioRepository.save(e);
    }

    @Transactional
    public void deleteEsercizio(Long id, Utente utente){
        Esercizio e = esercizioRepository.findPerModifica(id).orElseThrow(() -> new NotFoundException("esercizio non trovato"));
        checkAutore(e,utente);
        if (esercizioRoutineRepository.existsByEsercizio_Id(id)) {
            throw new NotValidException("L’esercizio è usato in una routine. Rimuovilo dalle routine prima di eliminarlo.");
        }
        esercizioRepository.delete(e);
        String nomeFoto = e.getPercorsoImmagine();
        if (nomeFoto != null) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        Files.deleteIfExists(Path.of(cartellaFoto).resolve(nomeFoto));
                    } catch (IOException ex) {
                        LoggerFactory.getLogger(EsercizioService.class).warn("Impossibile eliminare la foto {}", nomeFoto, ex);
                    }
                }
            });
        }
    }

    private void salvaFoto(Esercizio esercizio, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            return;
        }
        if (foto.getSize() > 4 * 1024 * 1024) {
            throw new NotValidException("La foto deve pesare al massimo 4 MB.");
        }
        String estensione;
        if ("image/jpeg".equals(foto.getContentType())) {
            estensione = ".jpg";
        } else if ("image/png".equals(foto.getContentType())) {
            estensione = ".png";
        } else {
            throw new NotValidException("Scegli una foto JPEG o PNG.");
        }
        String nome = UUID.randomUUID() + estensione;
        Path cartella = Path.of(cartellaFoto);
        try (InputStream input = foto.getInputStream()) {
            Files.createDirectories(cartella);
            Files.copy(input, cartella.resolve(nome));
            esercizio.setPercorsoImmagine(nome);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile salvare la foto", e);
        }
    }


    private void checkAutore(Esercizio esercizio, Utente utente){
        if(!esercizio.getAutore().getId().equals(utente.getId())){
            throw new NotAllowedException("Non si possono modificare esercizi di utenti diversi dall'utente correntemente loggato.");
        }
    }

}
