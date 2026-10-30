package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.dto.RoutineFormDto;
import it.uniroma3.siw.wefit.exception.NotFoundException;
import it.uniroma3.siw.wefit.model.Routine;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.RoutineService;
import it.uniroma3.siw.wefit.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoutineController {

    @Autowired
    RoutineService routineService;
    @Autowired
    UtenteService utenteService;

    @GetMapping("/routines")
    public String visualizzaRoutines(Model model, Authentication authentication){
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("utente",utente);
        model.addAttribute("routines", routineService.getAllRoutines());
        model.addAttribute("bookmarkedIds", routineService.getBookmarkedRoutineIds(utente));
        return "paginaListaRoutines";
    }

    @GetMapping("routines/{id}")
    public String visualizzaRoutine(@PathVariable Long id, Model model,Authentication authentication){
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("utente", utente);
        model.addAttribute("routine", routineService.getRoutine(id));
        model.addAttribute("bookmarkedIds", routineService.getBookmarkedRoutineIds(utente));
        return "dettaglioRoutine";
    }

    @GetMapping("/routines/new")
    public String compilaRoutine(Model model){
        model.addAttribute("routine", new RoutineFormDto("","")); // le virgolette servono perché il record richiede che i campi siano compilati alla creazione
        return "formRoutine";
    }

    @PostMapping("/routines")
    public String creaRoutine(@Valid @ModelAttribute("routine") RoutineFormDto dto, BindingResult bindingResult, Authentication authentication, Model model) {
        Utente utente = getUtenteCorrente(authentication);
        if (bindingResult.hasErrors()) {
            model.addAttribute("utente", utente);
            return "formRoutine";
        }
        Routine routine = new Routine();
        routine.setNome(dto.nome());
        routine.setDescrizione(dto.descrizione());
        Routine r = routineService.createRoutine(routine, utente);
        return "redirect:/routines/" + r.getId() + "/esercizi/select";
    }

    @GetMapping("/routines/{id}/edit")
    public String modificaRoutine(@PathVariable Long id, Model model, Authentication authentication){
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("utente", utente);
        Routine routine = routineService.getRoutineUtente(utente, id);
        if (routine == null) {
            throw new NotFoundException("Routine non trovata");
        }
        model.addAttribute("routine", new RoutineFormDto(routine.getNome(),routine.getDescrizione())); //prendo i dati della routine esistente e li carico nel dto
        model.addAttribute("routineId",id); // id non sta nel dto per sicurezza, impedisce a un utente di modificarlo manualmente
        return "formRoutine";
    }

    @PostMapping("/routines/{id}")
    public String aggiornaRoutine(@PathVariable Long id,@Valid @ModelAttribute("routine") RoutineFormDto dto, BindingResult bindingResult, Authentication authentication, Model model){
        Utente utente= getUtenteCorrente(authentication);
        model.addAttribute("routineId", id);
        if(bindingResult.hasErrors()){
            model.addAttribute("utente",utente);
            return "formRoutine";
        }
        /*prendi i campi del dto*/
        Routine routine = new Routine();
        routine.setNome(dto.nome());
        routine.setDescrizione(dto.descrizione());
        Routine r = routineService.updateRoutine(id,routine,utente);
        return "redirect:/routines/" + r.getId();
    }

    @PostMapping("/routines/{id}/bookmark")
    public String salvaRoutine(@PathVariable Long id, @RequestParam(defaultValue = "dettaglio") String origine, Authentication authentication) {
        Utente utente = getUtenteCorrente(authentication);
        routineService.toggleBookmark(id, utente);
        if ("salvate".equals(origine)) {
            return "redirect:/area-personale/salvate";
        }
        if ("catalogo".equals(origine)) {
            return "redirect:/routines";
        }
        return "redirect:/routines/" + id;
    }

    @PostMapping("/routines/{id}/delete")
    public String eliminaRoutine(@PathVariable Long id, Authentication authentication) {
        Utente utente = getUtenteCorrente(authentication);
        routineService.deleteRoutine(id, utente);
        return Utente.ADMIN_ROLE.equals(utente.getRole()) ? "redirect:/admin/routines" : "redirect:/area-personale/routines";
    }

    private Utente getUtenteCorrente(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            return null;
        }

        return utenteService.findUtenteByUsername(authentication.getName());
    }
}
