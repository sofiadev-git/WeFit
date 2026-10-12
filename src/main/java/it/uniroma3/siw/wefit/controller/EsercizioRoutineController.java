package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.model.Esercizio;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.EsercizioRoutineService;
import it.uniroma3.siw.wefit.service.EsercizioService;
import it.uniroma3.siw.wefit.service.RoutineService;
import it.uniroma3.siw.wefit.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EsercizioRoutineController {

    @Autowired
    RoutineService routineService;

    @Autowired
    EsercizioService esercizioService;

    @Autowired
    EsercizioRoutineService esercizioRoutineService;

    @Autowired
    UtenteService utenteService;

    @GetMapping("/routines/{id}/esercizi")
    public String visualizzaEserciziRoutine(@PathVariable Long id, Model model, Authentication authentication){
        Utente utente= getUtenteCorrente(authentication);
        model.addAttribute("esercizi", esercizioRoutineService.getEserciziRoutine(id));
        return "dettaglioRoutine";
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
