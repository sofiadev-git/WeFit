package it.uniroma3.siw.wefit.controller;

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

@Controller
public class RoutineController {

    @Autowired
    RoutineService routineService;
    @Autowired
    UtenteService utenteService;

    @GetMapping("/routines")
    public String visualizzaRoutines(Model model){
        model.addAttribute("routines", routineService.getAllRoutines());
        return "paginaListaRoutines";
    }

    @GetMapping("routines/{id}")
    public String visualizzaRoutine(@PathVariable Long id, Model model){
        model.addAttribute("routine", routineService.getRoutine(id));
        return "/dettaglioRoutine";
    }

    @GetMapping("/routines/new")
    public String compilaRoutine(Model model){
        model.addAttribute("routine", new Routine());
        return "formRoutine";
    }

    @PostMapping("/routines")
    public String creaRoutine(@Valid @ModelAttribute("routine") Routine routine, BindingResult bindingResult, Authentication authentication, Model model){
        Utente utente = getUtenteCorrente(authentication);
        if(bindingResult.hasErrors()){
            model.addAttribute("utente", utente);
            return "formRoutine";
        }
        Routine r = routineService.createRoutine(routine,utente);
        return "redirect:/routines/" + r.getId();
    }

    @GetMapping("/routines/{id}/edit")
    public String modificaRoutine(@PathVariable Long id, Model model, Authentication authentication){
        Utente utente = getUtenteCorrente(authentication);
        model.addAttribute("utente", utente);
        model.addAttribute("routine", routineService.getRoutineUtente(utente, id));
        return "formModificaRoutine";
    }

    @PostMapping("/routines/{id}")
    public String aggiornaRoutine(@PathVariable Long id,@Valid @ModelAttribute("routine") Routine routine, BindingResult bindingResult, Authentication authentication, Model model){
        Utente utente= getUtenteCorrente(authentication);
        if(bindingResult.hasErrors()){
            model.addAttribute("utente",utente);
            return "formModificaRoutine";
        }
        Routine r = routineService.updateRoutine(id,routine,utente);
        return "redirect:/routines/" + r.getId();
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
