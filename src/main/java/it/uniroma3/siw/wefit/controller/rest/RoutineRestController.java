package it.uniroma3.siw.wefit.controller.rest;

import it.uniroma3.siw.wefit.dto.RoutineDetailsDto;
import it.uniroma3.siw.wefit.model.Utente;
import it.uniroma3.siw.wefit.service.RoutineService;
import it.uniroma3.siw.wefit.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/routines")
public class RoutineRestController {

    @Autowired
    private RoutineService routineService;
    @Autowired
    private UtenteService utenteService;

    @GetMapping("/{id}")
    public RoutineDetailsDto getRoutine(@PathVariable Long id, Authentication authentication){
        Utente utente = getUtenteCorrente(authentication);
        return routineService.getRoutineDetails(id,utente);
    }

    @PostMapping("/{id}/bookmark")
    public  RoutineDetailsDto toggleBookmark(@PathVariable Long id, Authentication authentication){
        Utente utente = utenteService.findUtenteByUsername(authentication.getName()); // mostra solo quelli di quell'utente
        return routineService.toggleBookmark(id,utente);

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
