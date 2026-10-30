package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.EsercizioService;
import it.uniroma3.siw.wefit.service.RoutineService;
import it.uniroma3.siw.wefit.service.UtenteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AreaController {

    private final UtenteService utenteService;
    private final EsercizioService esercizioService;
    private final RoutineService routineService;

    public AreaController(UtenteService utenteService, EsercizioService esercizioService, RoutineService routineService) {
        this.utenteService = utenteService;
        this.esercizioService = esercizioService;
        this.routineService = routineService;
    }

    @GetMapping({"/area-personale", "/admin"})
    public String area(Model model, Authentication authentication) {
        model.addAttribute("utente", utenteService.findUtenteByUsername(authentication.getName()));
        return "paginaArea";
    }

    @GetMapping({"/area-personale/routines", "/admin/routines"})
    public String mieRoutine(Model model, Authentication authentication) {
        Utente utente = utenteService.findUtenteByUsername(authentication.getName());
        model.addAttribute("utente", utente);
        model.addAttribute("routines", routineService.getRoutinesUtente(utente));
        return "paginaMieRoutine";
    }

    @GetMapping("/area-personale/salvate")
    public String routineSalvate(Model model, Authentication authentication) {
        Utente utente = utenteService.findUtenteByUsername(authentication.getName());
        model.addAttribute("utente", utente);
        model.addAttribute("routines", routineService.getBookmarkedRoutines(utente));
        return "paginaRoutineSalvate";
    }

    @GetMapping({"/area-personale/esercizi", "/admin/esercizi"})
    public String mieiEsercizi(Model model, Authentication authentication) {
        Utente utente = utenteService.findUtenteByUsername(authentication.getName());
        model.addAttribute("utente", utente);
        model.addAttribute("eserciziUtente", esercizioService.getEserciziUtente(utente));
        return "paginaMieiEsercizi";
    }
}
