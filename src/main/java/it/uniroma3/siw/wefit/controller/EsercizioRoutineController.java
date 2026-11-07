
package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.dto.EsercizioRoutineFormDto;
import it.uniroma3.siw.wefit.exception.NotAllowedException;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.EsercizioRoutine;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.EsercizioRoutineService;
import it.uniroma3.siw.wefit.service.EsercizioService;
import it.uniroma3.siw.wefit.service.RoutineService;
import it.uniroma3.siw.wefit.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class EsercizioRoutineController {

    @org.springframework.beans.factory.annotation.Value("${wefit.react-url}")
    private String reactUrl;

    @Autowired
    RoutineService routineService;

    @Autowired
    EsercizioService esercizioService;

    @Autowired
    EsercizioRoutineService esercizioRoutineService;

    @Autowired
    UtenteService utenteService;

    @GetMapping("/routines/{id}/esercizi")
    public String visualizzaEserciziRoutine(@PathVariable Long id, Model model, Authentication authentication) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = routineService.getRoutine(id);

        model.addAttribute("utente", utente);
        model.addAttribute("routine", routine);
        model.addAttribute("esercizi", esercizioRoutineService.getEserciziRoutine(id));
        model.addAttribute("bookmarkedIds", routineService.getBookmarkedRoutineIds(utente));

        model.addAttribute("allenamentoUrl", reactUrl + "/routines/" + id + "/avvia");
        return "dettaglioRoutine";
    }

    @GetMapping("/routines/{id}/esercizi/select")
    public String selezionaEserciziPerRoutine(@RequestParam(defaultValue = "0") int page, @PathVariable Long id, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = getRoutinePropria(id, utente);
        if(page<0){
            page=0;
        }
        model.addAttribute("utente", utente);
        model.addAttribute("routine", routine);
        model.addAttribute("eserciziDisponibili", esercizioService.getEserciziDisponibili(utente,page));

        return "selezionaEserciziRoutine";
    }

    @GetMapping("/routines/{id}/esercizi/new")
    public String compilaEsercizioRoutine(@PathVariable Long id, @RequestParam(required = false) Long esercizioId, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = getRoutinePropria(id, utente);


        // Verifico che l'esercizio preselezionato sia accessibile.
        Esercizio esercizioSelezionato = null;

        if (esercizioId != null) {
            esercizioSelezionato = esercizioService.getEsercizioDefaultOrAutore(esercizioId, utente);
        }

        EsercizioRoutineFormDto dto = new EsercizioRoutineFormDto(esercizioId, null, null);

        model.addAttribute("utente", utente);
        model.addAttribute("routine", routine);
        model.addAttribute("routineId", id);
        model.addAttribute("esercizioRoutine", dto);
        model.addAttribute("eserciziDisponibili", esercizioService.getEserciziDisponibili(utente));
        model.addAttribute("esercizioSelezionato", esercizioSelezionato);
        model.addAttribute("modifica", false);

        return "formEsercizioRoutine";
    }

    @PostMapping("/routines/{id}/esercizi")
    public String creaEsercizioRoutine(@PathVariable Long id, @Valid @ModelAttribute("esercizioRoutine") EsercizioRoutineFormDto dto, BindingResult bindingResult, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = getRoutinePropria(id, utente);

        if (bindingResult.hasErrors()) {
            model.addAttribute("utente", utente);
            model.addAttribute("routine", routine);
            model.addAttribute("routineId", id);
            model.addAttribute("modifica", false);
            model.addAttribute("eserciziDisponibili", esercizioService.getEserciziDisponibili(utente));

            if (dto.esercizioId() != null) {
                Esercizio esercizioSelezionato = esercizioService.getEsercizioDefaultOrAutore(dto.esercizioId(), utente);
                model.addAttribute("esercizioSelezionato", esercizioSelezionato);
            }

            return "formEsercizioRoutine";
        }

        EsercizioRoutine esercizioRoutine = new EsercizioRoutine();
        esercizioRoutine.setNumeroSet(dto.numeroSet());
        esercizioRoutine.setRipetizioni(dto.ripetizioni());

        esercizioRoutineService.createEsercizioRoutine(id, dto.esercizioId(), esercizioRoutine, utente);

        return "redirect:/routines/" + id + "/esercizi/select";
    }

    @GetMapping("/routines/{id}/esercizi/{esId}/edit")
    public String modificaEsercizioRoutine(@PathVariable Long id, @PathVariable Long esId, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = getRoutinePropria(id, utente);
        EsercizioRoutine er = esercizioRoutineService.getEsercizioRoutine(id, esId);

        EsercizioRoutineFormDto dto = new EsercizioRoutineFormDto(er.getEsercizio().getId(), er.getNumeroSet(), er.getRipetizioni());

        model.addAttribute("utente", utente);
        model.addAttribute("routine", routine);
        model.addAttribute("routineId", id);
        model.addAttribute("esId", esId);
        model.addAttribute("esercizioRoutine", dto);
        model.addAttribute("esercizioSelezionato", er.getEsercizio());
        model.addAttribute("eserciziDisponibili", esercizioService.getEserciziDisponibili(utente));
        model.addAttribute("modifica", true);

        return "formEsercizioRoutine";
    }

    @PostMapping("/routines/{id}/esercizi/{esId}")
    public String aggiornaEsercizioRoutine(@PathVariable Long id, @PathVariable Long esId, @Valid @ModelAttribute("esercizioRoutine") EsercizioRoutineFormDto dto, BindingResult bindingResult, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        Routine routine = getRoutinePropria(id, utente);

        // Verifico che l'associazione appartenga alla routine indicata.
        esercizioRoutineService.getEsercizioRoutine(id, esId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("utente", utente);
            model.addAttribute("routine", routine);
            model.addAttribute("routineId", id);
            model.addAttribute("esId", esId);
            model.addAttribute("modifica", true);
            model.addAttribute("eserciziDisponibili", esercizioService.getEserciziDisponibili(utente));

            if (dto.esercizioId() != null) {
                Esercizio esercizioSelezionato = esercizioService.getEsercizioDefaultOrAutore(dto.esercizioId(), utente);
                model.addAttribute("esercizioSelezionato", esercizioSelezionato);
            }

            return "formEsercizioRoutine";
        }


        // Manteniamo il passaggio di EsercizioRoutine al service.
        EsercizioRoutine nuoviValori = new EsercizioRoutine();
        nuoviValori.setNumeroSet(dto.numeroSet());
        nuoviValori.setRipetizioni(dto.ripetizioni());


        esercizioRoutineService.updateEsercizioRoutine(id, esId, dto.esercizioId(), nuoviValori, utente);

        return "redirect:/routines/" + id + "/esercizi";
    }

    @PostMapping("/routines/{id}/esercizi/{esId}/delete")
    public String eliminaEsercizioRoutine(@PathVariable Long id, @PathVariable Long esId, Authentication authentication) {
        Utente utente = getUtenteCorrente(authentication);
        getRoutinePropria(id, utente);


        esercizioRoutineService.deleteEsercizioRoutine(id, esId, utente);

        return "redirect:/routines/" + id + "/esercizi";
    }

    private Utente getUtenteCorrente(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return utenteService.findUtenteByUsername(authentication.getName());
    }

    private Routine getRoutinePropria(Long id, Utente utente) {
        if (utente == null) {
            throw new NotAllowedException("Utente non autenticato");
        }

        Routine routine = routineService.getRoutineUtente(utente, id);

        if (routine == null) {
            throw new NotFoundException("Routine non trovata");
        }

        return routine;
    }
}
